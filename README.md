# Server
## Init
~~~
0. 기본 의존성
    - Spring Web, Spring HATEOAS, Spring JPA, H2, PostgreSQL, Lombok
1. pom.xml 복사 붙여넣기
2. Test DB 분리하기.
    - main/resource/application.properties
    - test/resource/application-test.properties
    - Project Structure/Module/application-test를 test scope에 추가.
~~~


# 시큐리티 매커니즘
1. 회원가입
    - Android에서 userinfo[post]로 유저생성 <br>
        1) qr_id
        2) 
2. if(생성 성공하면) 비밀번호를 설정하면서 인증을 진행. 
    - ClientId/ClientSecret, qr_id, password로 인증진행
3. 토큰 발급, Android에게 토큰 리턴
4. Android는 해당 access_token으로 인증하게됨.

## Memo
- Preference
  - Plug-in - lombok Plugin install
  - Annotation Processor - Enable (비동작 가능성 있기 때문.)
- Lombok Annotation을 이용, Build 할 때 알아서 해당 Annotation Code를 추가, Compile 같이 됨.
- @EqualIsAndHshCode of에 id를 준 이유<br> 
나중에 참조나 비교시 모든 변수를 가지고 작업이 이루어지면
      StackOverFlow 발생우려. 
      <br>id로만 진행하기 위해서 설정.
      @Data를 안쓰는 이유도, 들어가보면 @EqualsAndHashCode가 변수설정을 하지 않은 채 달려있음.<br>
      즉 상호참조와 관련하여 StackOvefFlow 발생가능성 있음.
 
 - Replace fo refectoring : "Spring" 블럭 지정 후 Option + Command + V , Local Variable
 - Autowired?
   - Springframework에서 지원하는 각 상황에 맞는 의존성 주입 Annotation.
 - ObjectMapper?
   - 데이터를 Json으로 바꿔주는 class.
 - mockMvc.perform(요청)
 - ResponseEntitly 가 뭔지? <br>
          통신 메시지 관련 header와 body의 값들을 하나의 객체로 저장하는 것이 HttpEntity class<br?
          Request 부분일 경우 HttpEntity를 상속받은 RequestEntity,<br>
          Response 부분일 경우 HttpEntity를 상속받은 ResponseEntity.<br>
 - Serialize : 직렬화, 객체를 전송가능한 Json 형태로 만드는 것.
 - Deserialize : 역직렬화, Json형태를 객채로 만드는 것.
 - assertThat ( p1, p2) : matcher에 의해 두 인자를 비교함. http://sejong-wiki.appspot.com/assertThat
 - Spring Docs 적용 : http://woowabros.github.io/experience/2018/12/28/spring-rest-docs.html
 - kakao login -> 자체 login을 내장톰켓으로 진행해야하는 이유 :
           MockBean으로 등록하여 테스트를 진행하면 127.0.0.1/oauth/token 으로 진입불가.
           가 아니고 서버켜놓고 테스트 돌리면된다 ^^ㅣ바려나  하 ㅈ 같네 
 
 
 
 ## Api 문서화
 - 요청 본문 문서화
 - 응답 본문 문서화
 - 링크 문서화
 - self
 - query-events
 - update-event
 - profile 링크 추가
 - 요청 헤더 문서화
 - 요청 필드 문서화
 - 응답 헤더 문서화
 - 응답 필드 문서화
 
 - Relaxed 접두어
   - 장점: 문서 일부분만 테스트 할 수 있다.
   - 단점: 정확한 문서를 생성하지 못한다.
   
   
### Flow
 - Event.class : Model data
 - EventDto.class : Model data의 Validate를 위한 semi-Model data.
 - EventController.cass : get,put request에 대한 response를 하기 위함.
 - EventResource.interface : Spring JPA 위한 interface.
 - EventResource.class : Hateoas를 위해 Link를 달아주기 위함.
 - EventStatus.class : Enum data.
 - EventValidator.class  :
 <br><br>
 - TestDescription.@interface : Annotation customize(@TestDescription)
  

### 단축키
 - 줄 단위 재정렬 ctrl + alt + I <br>
 - 필요없는 import 제거 : Ctrl + Option + O <br>
 - 변수로 빼내기 : option + Command + V <br>
 - 메소드로 빼내기 : option + command + M <br>
 - 해당 테스트 코드로 이동 : Command + Shift + T<br>
 - Select Method to Override/implement : ctrl + O <br>
 - 참고 : https://lalwr.blogspot.com/2018/04/intellij.html
 - 8080포트 죽이기 kill $(lsof -t -i:8080) 
   


### git ignore 안먹을 때
~~~
git rm --cached .
git add .
git commit -m "~~~~"
git push master  
~~~

### Todo
- Docs적용 :  http://woowabros.github.io/experience/2018/12/28/spring-rest-docs.html
AWs 키 바꾸기 
    
     