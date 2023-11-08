REST API służące do obsługi wniosków.
Ścieżka do aplikacji zaczyna się of /office, zaimplementowany enpoint to /proposal, przykładowe wywołanie: GET http://localhost:8080/office/proposal/list
Styl rozmieszczenia plików źródłowych to Package by Feature
Endpoint podzielony na dwie części, pierwsza to część wspólna, przyjmuje zapytania i przekazuje je do konkretnej bazy danych. Baze wybiera się podczas inicjalizacja proposalResource, musi dziedziczyć po IProposalResource. W części drugiej została zaimplementowana baza SQL na przykładzie MySQL.
Zapis historii mógłby odbywać się dynamicznie poprzez refleksje, a nie ręczny wybór pól, ale brakło czasu na implementacje takiego rozwiązania
Nie znam specyfikacji bazy, więc nie dodaje walidacji typowo bazowej, tzn. maksymalny rozmiar pola
W przyjmowanym jsonie wielkość znaków ma znaczenie. Klucze podajemy małymi literami, stany wniosków dużymi literami.
Projekt skupia się na funkcjonalności i przejrzystości, w razie potrzeby niektóre elementy można zmodyfikować tak, by mogłby być współdzielone przez inne endpointy, np. domyślne parametry w query

Założenia
- Autoryzacja i Autentykacja została już obsłużona.
- W zadaniu nie wspominano nic o informacjach kto wykonał operacje, jednak jest to na tyle istotna informacja, że została dodana. Na potrzeby projektu dane użytkownika zostały mockowane, w prawdziwym przypadku można by je pobrać np z JWT.
- Wnioski powinny być w jakiś sposób unikalne dla użytkownika (nie tylko ID), zakładam że to zostało w jakiś sposób rozwiązane
- Zakładam że w bazie danych stan wniosku jest prawidłową wartością
- Usuwanie wniosków z bazy danych wydaje się niepolecane, dlatego usunięcie to tylko zmiana stanu na DELETED, wyszukiwanie po list ignoruje te wyniki.
- Zakładam, że historia jest na tyle istotna, że znalazła się w dto
