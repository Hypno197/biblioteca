package org.generation.italy.model;

import java.time.LocalDate;

public class Libri {
	public int id, id_autore, id_genere, num_pagine, qnt, id_editore;
	public String titolo, autore, genere, editore;
	public LocalDate data_pubblicazione;
	@Override
	public String toString() {
		return "Libro codice: " + id + ", "+titolo+ "\nAutore :" + id_autore +" "+autore+ "\n Genere:" + id_genere + " "+genere+"\nNumero di Pagine:"
			+ num_pagine + ", Data Pubblicazione: "+data_pubblicazione+"\nQuantità disponibile: " + qnt + " pezzi.\nEditore: "+ id_editore + " " +editore;
	}
}
