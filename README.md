# Moneyfy

**Android 기반 가계부 앱**입니다.  
수입과 지출을 기록하고, 예산 설정과 소비 패턴 분석 기능을 제공합니다.

---

## 📱 개발 환경

- **Minimum SDK**: 26
- **Target SDK**: 35
- **언어**: Java (View 기반 UI)
- **Gradle 구조**: Version Catalog(`libs.versions.toml`) 사용
- **Jetpack Compose**: 사용하지 않음

---

## 📚 추가 라이브러리 및 기능

| 라이브러리 | 설명 | 버전 |
|-----------|------|------|
| MPAndroidChart | 원형/막대차트 구현용 | v3.1.0 |
| MaterialCalendarView | 월 단위 달력 뷰 | 1.4.3 |
| CoordinatorLayout | FAB, BottomSheet 등 상호작용 UI | 1.3.0 |

> ❌ `Kizitonwose CalendarView`는 **사용하지 않음**, 제거 가능

---

## 🔧 주요 커스터마이징 사항

- Jetpack Navigation (`navigation-fragment-ktx`, `navigation-ui-ktx`) 추가 libs.versions.toml 참조
- Lifecycle-aware 구성 (`lifecycle-runtime-ktx`) 명시: libs.versions.toml 참조
- Gradle의 Version Catalog를 통해 의존성과 플러그인을 통합 관리
- `jitpack.io` 추가 (외부 라이브러리 다운로드용)
- `gradle.properties`에 `android.nonTransitiveRClass=true` 설정

---

## ✅ 기타 참고 사항

- Java 11 환경에서 컴파일됨 (`sourceCompatibility = JavaVersion.VERSION_11`)
- `R` 클래스 최소화를 위해 `android.nonTransitiveRClass=true` 사용
- `pluginManagement`와 `repositoriesMode` 등 고급 Gradle 설정 적용

---

협업자는 프로젝트를 클론 후 반드시 **Gradle Sync**를 수행해 환경을 정상 반영해주세요.


---

# DB 연동

- Room 사용
- 홈 화면에서 지출 및 수입 항목을 작성, 수정, 삭제할 수 있으며, 모든 변경 사항은 DB에 반영

- 월별 카테고리별 지출, 수입 DB 차트 시각화 탭 반영

- 자산 관리 기능 제공
- 사용자가 자산 금액을 입력하고 저장 버튼을 누르면 DB에 저장
- 이후 동일 화면에서 수정 시 기존 데이터는 덮어쓰여 갱신

- 월 예산 관리
- 사용자가 월 예산을 입력하면, 앱은 현재 날짜를 기준으로 해당 월(yyyyMM)의 예산을 DB에 저장
- 동일 월에 이미 예산이 존재할 경우 자동으로 덮어쓰기 처리

- 목표 저축금액 설정
- 사용자가 시작일과 종료일, 목표 저축금액을 입력한 뒤 저장 버튼을 누르면 DB에 저장
- **저축은 사용자 일일 지출 내역에서 저축 카테고리를 선택하면 저축금액으로 판단**

- 동일한 기간(startDate, endDate)의 목표가 존재할 경우 덮어쓰기

- 앱 실행 시, 현재 날짜를 기준으로 다음 정보가 자동으로 표시
- 해당 월의 예산 (있을 경우)
- 현재 유효한 저축 목표 (오늘 날짜가 포함된 기간)
- 목표 저축금액에 대한 남은 금액 및 진행률

# 카테고리 추가

- 소비 카테고리 추가
- 수입 카테고리 (용돈, 근로소득, 금융소득) 추가

# 차트 탭 아이콘 해제
- 차트 탭 카테고리별 DB 연동 과정에서 충돌이 발생하여 카테고리 아이콘 연결 해제

