# Описание

Было проведено тестирование [приложения](artifacts/aqa-shop.jar) в двух вариантах окружения:

- с СУБД MySQL
- с СУБД PostgreSQL

В рамках тестирования были рассмотрены сценарии:

- с использованием набора карт, для которого [эмулятор банковских сервисов](artifacts/gate-simulator) генерирует предопределенные ответы
- сценарии, предназначенные для валидации полей ввода формы

# Количество тест-кейсов

Наборы тест-кейсов для каждого варианта окружения идентичны.

Общее количество тест кейсов: 62
Из них:
- для тестирования приобретения дебетовой картой: 31
- для тестирования приобретения в кредит: 31

# Процент успешных тестов: 66.12%

# Общие рекомендации

Оформлены в Github Issues к проекту с тегами: *documentation, invalid, enhancement*