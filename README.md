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
