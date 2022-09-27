package mvc.ver1.domain.web.frontcontroller.v3.controller;

import mvc.ver1.domain.member.Member;
import mvc.ver1.domain.member.MemberRepository;
import mvc.ver1.domain.web.frontcontroller.ModelView;
import mvc.ver1.domain.web.frontcontroller.v3.ControllerV3;

import java.util.Map;

import static mvc.ver1.domain.member.MemberRepository.getInstance;

public class MemberSaveControllerV3 implements ControllerV3 {

    private MemberRepository memberRepository = getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {

        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        ModelView mv = new ModelView("save-result");
        mv.getModel().put("member", member);

        return mv;
    }

}
