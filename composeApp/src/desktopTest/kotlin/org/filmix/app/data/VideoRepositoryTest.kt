package org.filmix.app.data

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.filmix.app.JVMPlatform
import org.filmix.app.app.Preferences
import kotlin.test.Test
import kotlin.test.assertEquals

class VideoRepositoryTest {

    private val platform = JVMPlatform()

    @Test
    fun `test get series`() {
        // Arrange
        val mockEngine = MockEngine { request ->
            respond(
                content = ByteReadChannel(
                    """
                    {
                      "id": 37794,
                      "section": 93,
                      "alt_name": "neznayka-i-veselye-chelovechki-1961",
                      "title": "Незнайка и веселые человечки",
                      "original_title": "",
                      "year": 1956,
                      "year_end": 1961,
                      "duration": 17,
                      "date": "23 май 2012",
                      "date_atom": "2012-05-23T03:06:35+04:00",
                      "favorited": false,
                      "watch_later": false,
                      "last_episode": {
                        "season": "1",
                        "episode": "1-7",
                        "translation": ""
                      },
                      "actors": [],
                      "found_actors": [
                        {
                          "id": 3621,
                          "name": "",
                          "original_name": "Hans Zimmer"
                        }
                      ],
                      "directors": [
                        "Петр Носов",
                        "Борис Дёжкин",
                        "Евгений Мигунов",
                        "Ефим Гамбург",
                        "Евгений Райковский",
                        "Борис Степанцев"
                      ],
                      "poster": "http:\/\/thumbs.filmixapp.cyou\/posters\/1221\/thumbs\/w220\/neznayka-i-veselye-chelovechki-1961_37794_0.jpg",
                      "short_story": "Сборник увлекательных мультфильмов, главными героями в которых выступают забавные маленькие человечки, живущие в волшебной стране: Незнайка, Карандаш, Винтик и Шпунтик, Мурзилка и многие другие персонажи. В сборник включены следующие мультфильмы: «Ровно в три пятнадцать», «Незнайка учится», «Что такое хорошо: приключения Мурзилки», «Винтик и Шпутник – веселые мастера», «Мурзилка и Великан».",
                      "player_links": {
                        "movie": [],
                        "playlist": {
                          "1": {
                            "Оригинал (русский)": {
                              "1": {
                                "link": "https:\/\/nl03.cdnsqu.com\/s\/FHdjcrIr1yI-noxBmlcS1-gkFBQUFBQUFBQUFBUklyQTBBQUEw.QoK2sAoujsP6b1rvwaEuhbg8R-_6VwCfRQWOeQ\/Neznayka-i-veselyye-chelovechki-1956-1961\/s01e01_%s.mp4?vs3-origin",
                                "qualities": [
                                  480
                                ]
                              },
                              "2": {
                                "link": "https:\/\/nl03.cdnsqu.com\/s\/FHdjcrIr1yI-noxBmlcS1-gkFBQUFBQUFBQUFBUklyQTBBQUEw.QoK2sAoujsP6b1rvwaEuhbg8R-_6VwCfRQWOeQ\/Neznayka-i-veselyye-chelovechki-1956-1961\/s01e02_%s.mp4?vs3-origin",
                                "qualities": [
                                  480
                                ]
                              },
                              "3": {
                                "link": "https:\/\/nl03.cdnsqu.com\/s\/FHdjcrIr1yI-noxBmlcS1-gkFBQUFBQUFBQUFBUklyQTBBQUEw.QoK2sAoujsP6b1rvwaEuhbg8R-_6VwCfRQWOeQ\/Neznayka-i-veselyye-chelovechki-1956-1961\/s01e03_%s.mp4?vs3-origin",
                                "qualities": [
                                  480
                                ]
                              },
                              "4": {
                                "link": "https:\/\/nl03.cdnsqu.com\/s\/FHdjcrIr1yI-noxBmlcS1-gkFBQUFBQUFBQUFBUklyQTBBQUEw.QoK2sAoujsP6b1rvwaEuhbg8R-_6VwCfRQWOeQ\/Neznayka-i-veselyye-chelovechki-1956-1961\/s01e04_%s.mp4?vs3-origin",
                                "qualities": [
                                  480
                                ]
                              },
                              "5": {
                                "link": "https:\/\/nl03.cdnsqu.com\/s\/FHdjcrIr1yI-noxBmlcS1-gkFBQUFBQUFBQUFBUklyQTBBQUEw.QoK2sAoujsP6b1rvwaEuhbg8R-_6VwCfRQWOeQ\/Neznayka-i-veselyye-chelovechki-1956-1961\/s01e05_%s.mp4?vs3-origin",
                                "qualities": [
                                  480
                                ]
                              },
                              "6": {
                                "link": "https:\/\/nl03.cdnsqu.com\/s\/FHdjcrIr1yI-noxBmlcS1-gkFBQUFBQUFBQUFBUklyQTBBQUEw.QoK2sAoujsP6b1rvwaEuhbg8R-_6VwCfRQWOeQ\/Neznayka-i-veselyye-chelovechki-1956-1961\/s01e06_%s.mp4?vs3-origin",
                                "qualities": [
                                  480
                                ]
                              },
                              "7": {
                                "link": "https:\/\/nl03.cdnsqu.com\/s\/FHdjcrIr1yI-noxBmlcS1-gkFBQUFBQUFBQUFBUklyQTBBQUEw.QoK2sAoujsP6b1rvwaEuhbg8R-_6VwCfRQWOeQ\/Neznayka-i-veselyye-chelovechki-1956-1961\/s01e07_%s.mp4?vs3-origin",
                                "qualities": [
                                  480
                                ]
                              }
                            }
                          }
                        },
                        "trailer": []
                      },
                      "kp_rating": "-",
                      "kp_votes": "-",
                      "imdb_rating": "-",
                      "imdb_votes": "-",
                      "serial_stats": {
                        "status": 0,
                        "comment": "",
                        "status_text": ""
                      },
                      "rip": "DVDRip 480",
                      "quality": "480",
                      "categories": [
                        "Семейный",
                        "Детский",
                        "Мультсериалы"
                      ],
                      "post_url": "https:\/\/filmix.fm\/multserialy\/semejnye\/37794-neznayka-i-veselye-chelovechki-1961.html",
                      "countries": [
                        "СССР"
                      ],
                      "relates": [
                        {
                          "title": "Веселый курятник",
                          "poster": "http:\/\/thumbs.filmixapp.cyou\/posters\/1221\/thumbs\/w140\/veselyy-kuryatnik-les-ptites-poules-multserial-2010_90040_0.jpg",
                          "category": "Детский",
                          "year": 2010,
                          "id": 90040,
                          "alt_name": "veselyy-kuryatnik-les-ptites-poules-multserial-2010"
                        },
                        {
                          "title": "Незнайка на Луне",
                          "poster": "http:\/\/thumbs.filmixapp.cyou\/posters\/1221\/thumbs\/w140\/neznayka-na-lune-1997_24519_0.jpg",
                          "category": "Мультфильмы",
                          "year": 1997,
                          "id": 24519,
                          "alt_name": "neznayka-na-lune-1997"
                        },
                        {
                          "title": "Незнайка учится",
                          "poster": "http:\/\/thumbs.filmixapp.cyou\/posters\/1221\/thumbs\/w140\/neznayka-uchitsya-1961_24523_0.jpg",
                          "category": "Мультфильмы",
                          "year": 1961,
                          "id": 24523,
                          "alt_name": "neznayka-uchitsya-1961"
                        },
                        {
                          "title": "Незнайка с нашего двора",
                          "poster": "http:\/\/thumbs.filmixapp.cyou\/posters\/1221\/thumbs\/w140\/neznayka-s-nashego-dvora-1983_24521_0.jpg",
                          "category": "Семейный",
                          "year": 1983,
                          "id": 24521,
                          "alt_name": "neznayka-s-nashego-dvora-1983"
                        }
                      ],
                      "rating": 71,
                      "rate_p": 92,
                      "rate_n": 21
                    }
                """.trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val httpClient = HttpClient(mockEngine)
        val fileCache = FileCache(platform)
        val preferences = mockk<Preferences>()
        every { preferences.deviceId } returns "test_device_id"
        every { preferences.getToken() } returns "test_token"
        val repository = VideoRepository(httpClient, fileCache, preferences, platform)

        // Act
        val video = runBlocking {
            repository.getVideo(37794)
        }

        // Assert
        assertEquals(37794, video.id)
        assertEquals(1, video.player_links.playlist.seasons.size)
        assertEquals(1, video.player_links.playlist.seasons[0].translations.size)
        assertEquals(7, video.player_links.playlist.seasons[0].translations[0].episodes.size)
    }

    @Test
    fun `test get video`() {
        // Arrange
        val mockEngine = MockEngine { request ->
            respond(
                content = ByteReadChannel(
                    """
                    {
  "id": 15522,
  "section": 0,
  "alt_name": "koronnyy-brosok-ernesta-slam-dunk-ernest-1995",
  "title": "Эрнест баскетболист",
  "original_title": "Slam Dunk Ernest",
  "year": 1995,
  "year_end": 0,
  "duration": 93,
  "date": "13 фев 2011",
  "date_atom": "2011-02-13T03:16:27+03:00",
  "favorited": false,
  "watch_later": false,
  "last_episode": null,
  "actors": [
    "Джим Варни",
    "Силк Козарт",
    "Карим Абдул-Джаббар",
    "Колин Лоуренс",
    "Мигель А. Нуньес мл.",
    "Лестер Барри",
    "Ричард Ликок",
    "Джей Бразо",
    "Стиви Вэлланс",
    "Аарон Джозеф"
  ],
  "found_actors": [
    {
      "id": 95222,
      "name": "Аарон Джозеф",
      "original_name": "Aaron Joseph"
    },
    {
      "id": 252,
      "name": "Джей Бразо",
      "original_name": "Jay Brazeau"
    },
    {
      "id": 14349,
      "name": "Джим Варни",
      "original_name": "Jim Varney"
    },
    {
      "id": 2928,
      "name": "Карим Абдул-Джаббар",
      "original_name": "Kareem Abdul-Jabbar"
    },
    {
      "id": 26278,
      "name": "Колин Лоуренс",
      "original_name": "Colin Lawrence"
    },
    {
      "id": 19541,
      "name": "Лестер Барри",
      "original_name": "Lester Barrie"
    },
    {
      "id": 3232,
      "name": "Мигель А. Нуньес мл.",
      "original_name": "Miguel A. Nunez Jr."
    },
    {
      "id": 37331,
      "name": "Ричард Ликок",
      "original_name": "Richard Leacock"
    },
    {
      "id": 3334,
      "name": "Силк Козарт",
      "original_name": "Cylk Cozart"
    },
    {
      "id": 80347,
      "name": "Стиви Вэлланс",
      "original_name": "Stevie Vallance"
    }
  ],
  "directors": [
    "Джон Р. Черри III"
  ],
  "poster": "http:\/\/thumbs.filmixapp.cyou\/posters\/1221\/thumbs\/w220\/koronnyy-brosok-ernesta-slam-dunk-ernest-1995_15522_0.jpg",
  "short_story": "Джим Варни – тот самый Эрнест – опять на наших экранах. Даже на баскетбольной площадке он умудряется смешить зрителей. Эрнест с детства мечтал быть баскетболистом, но все его броски приводили к ужасным последствиям, и никто не хотел его видеть в составе своей команды. Шли годы, мечты о мяче и баскетбольной корзине не оставляли его, хотя Эрнест работал обычным уборщиком в супермаркете, в компании со своими друзьями из баскетбольной команды любителей. Наконец, ему представился случай выйти на площадку, однако, даже не успев сделать это, он обеспечил поражение своей команде.",
  "player_links": {
    "movie": [
      {
        "link": "https:\/\/nl202.cdnsqu.com\/s\/FH0vWtbV98usJt4ZPKKdTjjkFBQUFBQUFBQUFBUklyQ0VnZ0Ew.TPmAAlBYGc5yExN9SspAp-iKBr2Xc3qMiXYR3A\/sd_480v\/Slam.Dunk.Ernest_[,,,,480,].mp4?vs0-origin",
        "translation": "Двухголосый"
      },
      {
        "link": "https:\/\/nl202.cdnsqu.com\/s\/FH0vWtbV98usJt4ZPKKdTjjkFBQUFBQUFBQUFBUklyQ0VnZ0Ew.TPmAAlBYGc5yExN9SspAp-iKBr2Xc3qMiXYR3A\/hd_30_nl107\/Slam.Dunk.Ernest.1995.DVO.DVDRip_[,,,,480,].mp4",
        "translation": "Двухголосый, P - Екатеринбург Арт"
      }
    ],
    "playlist": [],
    "trailer": []
  },
  "kp_rating": 4.904,
  "kp_votes": 343,
  "imdb_rating": 4.6,
  "imdb_votes": 2481,
  "serial_stats": null,
  "rip": "DVDRip 480",
  "quality": "480",
  "categories": [
    "Комедия"
  ],
  "post_url": "https:\/\/filmix.fm\/filmi\/komedia\/15522-koronnyy-brosok-ernesta-slam-dunk-ernest-1995.html",
  "countries": [
    "Канада",
    "США"
  ],
  "relates": [
    {
      "title": "Эрнест идет в тюрьму",
      "poster": "http:\/\/thumbs.filmixapp.cyou\/posters\/1221\/thumbs\/w140\/ernest-idet-v-tyurmu_1990_117485_0.jpg",
      "category": "Криминал",
      "year": 1990,
      "id": 117485,
      "alt_name": "ernest-idet-v-tyurmu_1990"
    },
    {
      "title": "Эрнест напугал дураков",
      "poster": "http:\/\/thumbs.filmixapp.cyou\/posters\/1221\/thumbs\/w140\/ernest-napugal-durakov-ernest-scared-stupid-1991_54577_0.jpg",
      "category": "Семейный",
      "year": 1991,
      "id": 54577,
      "alt_name": "ernest-napugal-durakov-ernest-scared-stupid-1991"
    },
    {
      "title": "Эрнест спасает Рождество",
      "poster": "http:\/\/thumbs.filmixapp.cyou\/posters\/1221\/thumbs\/w140\/yernest-spasaet-rozhdestvo-ernest-saves-christmas_7467_0.jpg",
      "category": "Семейный",
      "year": 1988,
      "id": 7467,
      "alt_name": "yernest-spasaet-rozhdestvo-ernest-saves-christmas"
    },
    {
      "title": "Невероятные приключения Эрнеста в Африке",
      "poster": "http:\/\/thumbs.filmixapp.cyou\/posters\/1221\/thumbs\/w140\/neveroyatnye-priklyucheniya-ernesta-v-afrike-ernest-goes-to-africa-1997_61328_0.jpg",
      "category": "Семейный",
      "year": 1997,
      "id": 61328,
      "alt_name": "neveroyatnye-priklyucheniya-ernesta-v-afrike-ernest-goes-to-africa-1997"
    },
    {
      "title": "Эрнест снова в седле",
      "poster": "http:\/\/thumbs.filmixapp.cyou\/posters\/1221\/thumbs\/w140\/ernest-snova-v-sedle-ernest-rides-again-1993_76559_0.jpg",
      "category": "Семейный",
      "year": 1993,
      "id": 76559,
      "alt_name": "ernest-snova-v-sedle-ernest-rides-again-1993"
    }
  ],
  "rating": 14,
  "rate_p": 21,
  "rate_n": 7
}
                """.trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val httpClient = HttpClient(mockEngine)
        val fileCache = FileCache(platform)
        val preferences = mockk<Preferences>()
        every { preferences.deviceId } returns "test_device_id"
        every { preferences.getToken() } returns "test_token"
        val repository = VideoRepository(httpClient, fileCache, preferences, platform)

        // Act
        val video = runBlocking {
            repository.getVideo(15522)
        }

        // Assert
        assertEquals(15522, video.id)
        assertEquals(2, video.player_links.movie.size)
    }

    @Test
    fun `test get video without relates`() {
        // Arrange
        val mockEngine = MockEngine { request ->
            respond(
                content = ByteReadChannel(
                    """
                    {
  "id": 123113,
  "section": 0,
  "alt_name": "legenda-zhemchuga-nagi-2017",
  "title": "Легенда жемчуга Наги",
  "original_title": "Jiao zhu zhuan",
  "year": 2017,
  "year_end": 0,
  "duration": 108,
  "date": "3 янв 2018",
  "date_atom": "2018-01-03T12:59:46+03:00",
  "favorited": false,
  "watch_later": false,
  "last_episode": null,
  "actors": [
    "Даррен Ван",
    "Саймон Ям",
    "Tian'Ai Zhang",
    "Шэн Гуаньсэнь",
    "Ван Сюнь",
    "Цзян Луся",
    "Хэ Суй",
    "Син Юй",
    "Дин Лю",
    "Чжао Цзянь"
  ],
  "found_actors": [
    {
      "id": 5467,
      "name": "Саймон Ям",
      "original_name": "Simon Yam"
    }
  ],
  "directors": [
    "Леон Ян"
  ],
  "poster": "http:\/\/thumbs.filmixapp.cyou\/posters\/1221\/thumbs\/w220\/legenda-zhemchuga-nagi-2017_123113_0.jpg",
  "short_story": "Много лет назад была война между людьми и крылатыми обитателями небес. Люди победили своих врагов, после чего изгнали их с материка и предали огню прекрасный город Уранополис. Последний наследник крылатых вождей однажды узнаёт способ уничтожить ненавистных людишек.",
  "player_links": {
    "movie": [
      {
        "link": "https:\/\/nl03.cdnsqu.com\/s\/FHY1dQ7gc7ziX3MiUL80yA3EFBQUFBQUFBQUFBUklnU3d3d0JV.__ay220IS4GueqUa-I-T1H3zJOvm3CGAkIG2QQ\/hd_30_nl03\/Legend.of.the.Naga.Pearls.2017.MVO.WEBRip.2160p_[2160,1440,1080,720,480,].mp4?vs3-origin",
        "translation": "Многоголосый, L - Колобок"
      }
    ],
    "playlist": [],
    "trailer": []
  },
  "kp_rating": 6.303,
  "kp_votes": 694,
  "imdb_rating": 5.5,
  "imdb_votes": 989,
  "serial_stats": null,
  "rip": "WEB-DLRip 2160",
  "quality": "2160",
  "categories": [
    "Фэнтези",
    "Мелодрама",
    "Приключения",
    "Боевики",
    "Комедия"
  ],
  "post_url": "https:\/\/filmix.fm\/filmi\/fjuntezia\/123113-legenda-zhemchuga-nagi-2017.html",
  "countries": [
    "Китай"
  ],
  "relates": false,
  "rating": 170,
  "rate_p": 255,
  "rate_n": 85
}
                """.trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val httpClient = HttpClient(mockEngine)
        val fileCache = FileCache(platform)
        val preferences = mockk<Preferences>()
        every { preferences.deviceId } returns "test_device_id"
        every { preferences.getToken() } returns "test_token"
        val repository = VideoRepository(httpClient, fileCache, preferences, platform)

        // Act
        val video = runBlocking {
            repository.getVideo(123113)
        }

        // Assert
        assertEquals(123113, video.id)
        assertEquals(1, video.player_links.movie.size)
    }
}