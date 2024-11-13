package cctv.Service;

import cctv.DTO.MemberDTO;
import cctv.Entity.Member;
import cctv.Repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member get(String email, String provider) {
        log.info("qwer");
        return memberRepository.findUserByEmailAndProvider(email, provider)
                .orElseThrow(() -> new NoSuchElementException("Member not found with email: " + email));
    }
    @Transactional
    public Member update(String email, String provider, MemberDTO memberDTO){
        log.info("MemberDTO 정보: {}", memberDTO);
        Member target = memberRepository.findUserByEmailAndProvider(email, provider).orElseThrow(() -> new NoSuchElementException("Member not found with email: " + email));
        target = target.update(memberDTO);
        return target;
    }
}