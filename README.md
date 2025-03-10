<h1>Game Library</h1>
<p><b>Game Library</b> — это REST API для управления игровой библиотекой</p>
<h2>Функционал</h2>
<o><li>Поиск игры по названию</li>
   <li>Поиск игры по информации о компании</li>
   <li>Удаление и добавление игр в библиотеку</li>
   <li>Мониторинг цен на игры в различных регионах</li>
   <li>Просмотр игр от определенного издателя</li>
   <li>Создание коллекций игр по выбранной категории</li>
</o>
<h2>Использованные технологии</h2>
<o><li>Java 17</li>
   <li>Maven</li>
   <li>Spring Boot</li>
   <li>Spring Web</li>
   <li>REST API</li></o>
<h2>REST API</h2>
<p>Поддержка HTTP метода GET c Query и Path Parametrs</p> </ol><h2>Примеры запросов к API</h2> <p>Ниже приведены примеры запросов к API:</p><h3>Поиск игры по названию</h3> <pre><code>GET /games?title=НазваниеИгры</code></pre> <p>Пример:</p> <pre><code>GET /games?title=The Witcher 3</code></pre><h3>Поиск игры по информации о компании</h3> <pre><code>GET /games?company=НазваниеКомпании</code></pre> <p>Пример:</p> <pre><code>GET /games?company=CD Projekt Red</code></pre><h3>Добавление игры в библиотеку</h3> <pre><code>POST /games Content-Type: application/json
{
"title": "НазваниеИгры",
"company": "НазваниеКомпании",
"price": 59.99,
"region": "EU"
}</code></pre>

<p>Пример:</p> <pre><code>POST /games Content-Type: application/json
{
"title": "Cyberpunk 2077",
"company": "CD Projekt Red",
"price": 49.99,
"region": "US"
}</code></pre>

<h3>Удаление игры из библиотеки</h3> <pre><code>DELETE /games/{id}</code></pre> <p>Пример:</p> <pre><code>DELETE /games/1</code></pre><h3>Просмотр игр от определенного издателя</h3> <pre><code>GET /games/publisher?name=НазваниеИздателя</code></pre> <p>Пример:</p> <pre><code>GET /games/publisher?name=Electronic Arts</code></pre><h3>Создание коллекции игр по категории</h3> <pre><code>POST /collections Content-Type: application/json
{
"name": "НазваниеКоллекции",
"category": "Категория",
"gameIds": [1, 2, 3]
}</code></pre>

<p>Пример:</p> <pre><code>POST /collections Content-Type: application/json
{
"name": "Лучшие RPG",
"category": "RPG",
"gameIds": [1, 2, 3]
}</code></pre>

<h2>Мониторинг цен на игры</h2> <p>Для мониторинга цен на игры в различных регионах используйте следующий запрос:</p> <pre><code>GET /games/price?region=Регион</code></pre> <p>Пример:</p> <pre><code>GET /games/price?region=US</code></pre><h2>Лицензия</h2> <p>Этот проект распространяется под лицензией MIT. Подробнее см. в файле <a href="LICENSE">LICENSE</a>.</p><h2>Авторы</h2> <ul> <li>Ваше Имя - <a href="https://github.com/ваш-профиль">GitHub</a></li> </ul><h2>Благодарности</h2> <p>Спасибо всем, кто поддерживал и вдохновлял на создание этого проекта!</p><h2>Контрибуция</h2> <p>Если вы хотите внести свой вклад в проект, пожалуйста, создайте pull request. Мы рады любым улучшениям и предложениям!</p><h2>Контакты</h2> <p>Если у вас есть вопросы или предложения, свяжитесь со мной через <a href="mailto:ваш-email@example.com">email</a>.</p>
