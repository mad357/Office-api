REST API służące do obsługi wniosków.
Ścieżka do aplikacji zaczyna się of /office, zaimplementowany enpoint to /proposal, przykładowe wywołanie: GET http://localhost:8080/office/proposal/list
Styl rozmieszczenia plików źródłowych to Package by Feature
Enpoint podzielony na dwie części, pierwsza to część wspólna, przyjmuje zapytania i przekazuje je do konkretnej bazy danych. Baze wybiera się podczas inicjalizacja proposalResource, musi dziedziczyć po IProposalResource. Została zaimplementowana baza SQL na przykładzie MySQL.



Założenia
- Autoryzacja i Autentykacja została już obsłużona.
- W zadaniu nie wspominano nic o informacjach kto wykonał operacje, jednak jest to na tyle istotna informacja, że została dodana. Na potrzeby projektu dane użytkownika zostały mockowane, w prawdziwym przypadku można by je pobrać np z JWT.
- 
