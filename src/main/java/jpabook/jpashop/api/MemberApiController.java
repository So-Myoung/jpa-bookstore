package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    //--------------회원 등록 api---------------
    /*
    * V1: 요청 값으로 Member 엔티티를 직접 받음.
    * -> 엔티티가 변경되면 api 스펙이 변하므로, 절대 지양
    * */
    @PostMapping("/api/create/members/v1")
    public CreateMemberResponse createMemberV1(@RequestBody @Valid Member member){

        Long memberId = memberService.join(member);

        return new CreateMemberResponse(memberId);
    }

    /*
     * V2: 요청 값으로 Member 엔티티 대신에 별도의 DTO를 받는다.
     * */
    @PostMapping("/api/create/members/v2")
    public CreateMemberResponse createMemberV2(@RequestBody @Valid CreateMemberRequest request){

        Member member = new Member();
        member.setName(request.getName());
        member.setAddress(request.getAddress());

        Long memberId = memberService.join(member);
        return new CreateMemberResponse(memberId);
    }
    /*
    Request:
    {
        "name" : "nameA",
        "address": {
                    "city": "인천",
                    "street" : "streetA",
                    "zipcode" : "11111"
                    }
    }
    */

    //--------------회원 수정 api---------------
    /*
     * V2: 수정 api
     * */
    @PostMapping("/api/update/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long memberId,
                                               @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(memberId, request.getName(), request.getAddress());
        Member findMember = memberService.findOne(memberId);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName(), findMember.getAddress());
    }

    //--------------회원 조회 api---------------
    /*
     * V1: 응답 값으로 엔티티를 직접 외부에 노출
     * -> 엔티티의 모든 값이 노출, 엔티티가 변경되면 api 스펙이 변함. 절대 지양
     * */
    @GetMapping("/api/members/v1")
    public List<Member> MemberV1(){
        return memberService.findMembers();
    }

    /*
     * V2: 응답 값으로 엔티티가 아닌 별도의 DTO를 반환
     * */
    @GetMapping("/api/members/v2")
    public Result MemberV2(){

        List<Member> findMembers = memberService.findMembers();

        // 엔티티 -> DTO 변환
        List<MemberDto> MemberList = findMembers.stream()
                .map(m -> new MemberDto(m.getName(), m.getAddress()))
                .collect(Collectors.toList());

        return new Result(MemberList);
    }

    //--------회원 등록 dto-----------
    @Data
    static class CreateMemberRequest {
        private String name;
        private Address address;
    }
    @Data
    @AllArgsConstructor
    static class CreateMemberResponse {
        private Long id;
    }

    //--------회원 수정 dto-----------
    @Data
    static class UpdateMemberRequest {
        private String name;
        private Address address;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
        private Address address;
    }

    //--------회원 조회 dto-----------
    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
        private Address address;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}