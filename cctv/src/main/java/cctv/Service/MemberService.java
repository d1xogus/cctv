package cctv.Service;

import cctv.DTO.MemberDTO;
import cctv.Entity.Member;
import cctv.Entity.Role;
import cctv.Repository.MemberRepository;
import cctv.Repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    public Member get(String email) {
        return memberRepository.findUserByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Member not found with email: " + email));
    }

    @Transactional
    public Member update(String email, MemberDTO memberDTO){
        log.info("MemberDTO 정보: {}", memberDTO);
        Member target = memberRepository.findUserByEmail(email).orElseThrow(() -> new NoSuchElementException("Member not found with email: " + email));
        if (memberDTO.getRoleId() != null) {
            Role newRole = roleRepository.findById(memberDTO.getRoleId())
                    .orElseThrow(() -> new NoSuchElementException("Role not found with id: " + memberDTO.getRoleId()));
            target.setRole(newRole);
        }
        if (memberDTO.getName() != null){
            target.setName(memberDTO.getName());
        }
        if (memberDTO.getPhone() != null){
            target.setPhone(memberDTO.getPhone());
        }
        return target;
    }

    @Transactional
    public void delete(String email) {
        Member target = memberRepository.findUserByEmail(email).orElseThrow(() -> new NoSuchElementException("Member not found with email: " + email));
        memberRepository.delete(target);
    }
}
