# Реализация алгоритмов индексирования и ранжирования документов
---
### Список доступных команд:
* `--help` (Выводит список доступных команд.)
* `--find [query]` (Поиск слова в индексе.)
* `--load [directory]` (Загрузка файлов индекса из директории.)
* `--index [directory]` (Индексация указанной директории.)
* `--exit` (Выход из приложения.)

### Алгоритм:
1. Получаем список файлов в директории.
2. В цикле загружаем каждый файл
3. Построчно читаем файл, разделяя слова по RegExp-шаблону `"\b[A-Za-zА-Яа-я]+\b"`
4. Сохраняем данные в 3-х коллекциях.
    1. Коллекция 1: `HashMap<Integer, Integer>`
    
    |Id документа `(ключь)`|Информация о документе `(значение)`|
    |-|-|
    
    2. Коллекция 2: `HashMap<Integer, Integer>`
    
    |Термин `(ключь)`|ID термина `(значение)`|
    |-|-|
    
    3. Коллекция 3: `HashMap<Integer, LinkedHashMap<Integer, TermFriguency>` (сортирован) `>`
    
    |ID термина `(ключь)`|ID документа `(ключь)`|Информация о термине `(значение)`|
    |-|-|-|
