# Школьный Дневник (Планировщик ДЗ)

Стильный, автономный планировщик домашних заданий для школьников с поддержкой всех предметов белорусской школьной программы и дизайном в стиле матового стекла (Glassmorphism & Grey).

## Особенности проекта
- **Стеклянный дизайн (Glassmorphism):** полупрозрачные карточки с градиентной окантовкой, тактильные пружинные анимации кнопок, круговой индикатор прогресса, строгая серо-графитовая палитра.
- **Все предметы белорусских школ (1–11 классы):** Беларуская мова, Беларуская літаратура, Русский язык, Алгебра, Геометрия, Физика, Химия, История Беларуси и другие.
- **Автономность:** локальная база данных Room (SQLite), работает без интернета и без необходимости входа.
- **Готовность к сборке:**
  - `minSdk = 26` (Android 8.0)
  - `targetSdk = 30`
  - `compileSdk = 36`
  - Настроен GitHub Actions CI (`.github/workflows/android.yml`) для автоматической компиляции APK на GitHub.
  - Присутствует сгенерированный исполняемый `gradlew` и `gradle/wrapper/gradle-wrapper.jar`.
  - Присутствует `app/google-services.json` для предотвращения ошибок сборки.

---

## Сборка на GitHub Actions (Автоматически)
При пуше в репозиторий GitHub автоматически запустит workflow `.github/workflows/android.yml`:
1. Установит JDK 17 (Temurin).
2. Настроит Android SDK 36 (`build-tools;36.0.0`, `platforms;android-36`, `cmdline-tools;latest`).
3. Примет лицензии SDK (`yes | sdkmanager --licenses`).
4. Соберёт отладочный APK: `./gradlew assembleDebug`.
5. Прикрепит готовый файл `SchoolPlanner-APK` в разделе **Actions** -> **Artifacts** для скачивания на телефон.

---

## Сборка локально на ПК (Linux / macOS / Windows)

### 1. Требования
- **Java JDK 17** (`java -version`)
- **Android SDK** с установленными:
  - `platform-tools`
  - `platforms;android-36`
  - `build-tools;36.0.0`

### 2. Настройка `local.properties`
Создайте в корне проекта файл `local.properties` (скопировав из `local.properties.example`):
```properties
sdk.dir=/home/username/Android/Sdk
```
*(Замените на реальный путь к вашему Android SDK)*

### 3. Сборка APK
```bash
chmod +x gradlew
./gradlew assembleDebug
```
Готовый APK будет в папке:
`app/build/outputs/apk/debug/app-debug.apk`

---

## Открытие и сборка в CodeAssist (на телефоне)
1. Склонируйте репозиторий или импортируйте проект в **CodeAssist**.
2. В настройках CodeAssist убедитесь, что выбран JDK 17.
3. Нажмите **Build / Сборка** — проект соберется с первого раза без конфликтов.
