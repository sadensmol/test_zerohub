## Java developer challenge ##

ToDo:
1. Create converter service that support minimum two cross currencies to get rate (e.g.: BTC/RUB = BTC->EUR->USD->RUB)
2. Implement gRPC service RatesService
3. Complete test suite against implemented gRPC service (via inprocess communication)
4. Use BigDecimal, be care with divide operation

As additional tasks you may extend test suite and service for following cases:
1. Test conversion small values (check rounding)
2. Catch exceptions if rate couldn't be found
3. Find minimal rate with 1 cross currency limit (example: BTC->USD = 60000, BTC->LTC->USD = 58000)

Pre - Final review:
1. Ожидали конечно двунаправленный граф для решения этой задачи (видели todo)
3. Есть идея почему ./gradlew test не выводит отчеты по тестам? На java всё от отрабатывает.

Final review
По прежнему не пофикшена проблема с атомарностью операций removeAll и add. Плюс код стал еще более медленный из-за использования copyOnWrite структур.
В целом гораздо проще было бы использовать графы. Но даже с текущей реализацией переделать conversions на ConcurrentHashMap с доступом по ключу и не было бы проблем с атомарностью и скорость построения conversionChain была бы быстрее с доступом по ключу и бинарным поиском, чем фильтровать список.

Plan:
- move to graph structure
- finish with all ToDo items in the list
- fix final review problems, add improvements
- add security configuration
- figure out how to deal with exceptions in controller 
- refactor tests