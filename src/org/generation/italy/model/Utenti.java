package org.generation.italy.model;

import java.time.LocalDate;

public class Utenti {
	public int id;
	public String nome, cognome, indirizzo, num_telefono, cod_fiscale;
	public LocalDate data_nascita;
	@Override
	public String toString() {
		return "Utente numero " + id + ": " + nome+" "+ cognome +"\nIndirizzo: " + indirizzo
				+ "\nNumero di telefono: " + num_telefono + "\nCodice Fiscale: " + cod_fiscale + "\nData di nascita: " + data_nascita;
	}

}
