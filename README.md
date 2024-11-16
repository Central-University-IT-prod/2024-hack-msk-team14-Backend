# PRODeli

Разделяй (долги) и властвуй!

## Фичи
1. Есть возможность зарегистрировать счёт и поделиться им с друзьями
2. Возможность отсканировать чек при помощи камеры
3. Реализация через Telegram WebApps (удобный доступ к приложению)
4. Напоминания об оплате через 3 дня
5. Возможность подтвердить перевод средств
6. Возможность отследить, кто кому сколько должен

## Запуск dev-версии (backend)

1. Клонирование репозитория

```bash
git clone git@{{sensitive_data}}:prod-team-14/backend.git
```

2. Указать необходимые переменные среды (или оставить по умолчанию)

3. Запуск
```bash
./gradlew bootRun
```

## Запуск prod-версии (backend)

1. Клонирование репозитория

```bash
git clone git@{{sensitive_data}}:prod-team-14/backend.git
```

2. Указать необходимые переменные среды (или оставить по умолчанию)

3. Сборка образов Docker:
```bash
docker compose build
```

4. Выключение старой версии:
```bash
docker compose down
```

5. Запуск новой версии:
```bash
docker compose up -d
```

## Используемые технологии

1. PostgreSQL
2. Spring
3. Kotlin
4. Gradle

## Схема сущностей в БД

![prodeli-application.png](static%2Fprodeli-application.png)
