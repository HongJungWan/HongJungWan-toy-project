package mvc.ver1.web.frontcontroller.v3.controller;

import mvc.ver1.domain.member.Member;
import mvc.ver1.domain.member.MemberRepository;
import mvc.ver1.web.frontcontroller.ModelView;
import mvc.ver1.web.frontcontroller.v3.ControllerV3;

import java.util.List;
import java.util.Map;

public class MemberListControllerV3 implements ControllerV3 {
    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {

        List<Member> members = memberRepository.findAll();

        ModelView mv = new ModelView("members");
        mv.getModel().put("members", members);

        return mv;
    }

}
