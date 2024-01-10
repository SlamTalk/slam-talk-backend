# slam-talk-BackEnd

###  📌 브랜치 전략
```json
main ↔ develop ↔ feature
```        
- **main** : 코드리뷰 끝난 코드만, `스프린트 마지막`
- **develop**  : 모든 개발 변경 사항 저장 (배포 전)
- **feature**  : 기능별 브런치 생성
  - 개발 시 : ex) **`feature/이슈번호-chatting`**
  - 개인 레포 개발 완료 후
      - pull request feature/123-chatting → develop


- 🐥 브랜치명 예시

  `feature/123-chatting`
  </br>
  `style/`
  </br>
  `fix/`
  </br>
  `hotfix/`


### 📌 github repository 개발 순서
1. 깃허브 slam-talk-frontend 리포지토리를 **clone**
2. 기능 명세서에 해당하는 기능 이슈 생성
3. 내가 개발할 기능에 해당하는 브랜치 생성
   - feature/이슈번호-terminal
   - feature/이슈번호-filetree
4. 개발 후 pull request - Slam Talk frontend repository develop
5. **코드리뷰** 👀✨
   - 24시간 안에 확인하기
   - 한 명 이상이 확인하면 merge 가능
   - 요청시 모여서 코드리뷰
6. develop 브랜치에 merge
7. 본인 리포지토리 development branch → pull



#### 🚨 주의 사항
- **이슈 단위로 PR!!!**
- 최소 3일에 한번은 pull request + 코드리뷰 하기


### 📌 커밋 메시지 규칙
🐥 예시
```json
feat: some new feature
feat: some other feature
docs: some docs update
feat: some different feature
```

**`feat`:** 새로운 기능(A new feature)

**`fix`:** 버그 고침(A bug fix) ✅ 한 줄 수정도 해당

**`refactor`:** 버그를 고치거나 기능을 추가하지 않은 코드 변화 (A code change that neither fixes a bug nor adds a feature)

**`style`**: UI관련, CSS, 스타일만 수정 시

**`build`:** 빌드 시스템이나 외부 의존성에 영향을 주는 변화 (Changes that affect the build system or external dependencsies/ example scopes: gulp, broccoli, npm)

**`docs`:** 문서에만 변화가 있음(Documentation only changes) ✅ 리드미 수정 시 docs

**`ci`:** CI 환경 설정 파일이나 스크립트의 변화 (Changes to our CI configuration files and scripts/ example: CircleCi, SauceLabs)

**`test`:** 빠진 테스트 추가 혹은 존재하는 테스트 고침 (Adding missing tests or correcting exisiting tests)

**`install`**: 환경 설정 시 (install시)