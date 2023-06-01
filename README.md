# Описание API

* http://localhost:8085/api/articles/all - выдает все сохраненные в БД статьи, только описания
* http://localhost:8085/api/articles/site?news_site=<название_новостного_сайта> - выдает список статей по новостному сайту, только описания
* http://localhost:8085/api/articles/id/<идентификатор_статьи> - выдает конкретную статью по идентификатору

# Описание параметров

* startup.thread.count - колличество потоков
* thread.duration.sleep.seconds - задержка потока между циклами чтения
* articles.limit_for_all - общее количество скачиваемых записей
* articles.limit_for_thread - количество записей, скачиваемых одним потоком за один цикл работы
* articles.black_list - черный список слов, через запятую 
* source.news.url - URL источника новостей
* news.buffer.max_count_for_site - максимальный размер буфера новостей для каждого сайта