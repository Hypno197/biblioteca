package org.generation.italy.model;

import java.time.LocalDate;

public class Prestito {
	public int id, id_utente, id_libro;
	public boolean riconsegnato, ritardo;
	public LocalDate data_inizio, data_fine, data_consegna;
	@Override
	public String toString() {
		return "Prestito [id=" + id + ", id_utente=" + id_utente + ", id_libro=" + id_libro + ", riconsegnato="
				+ riconsegnato + ", ritardo=" + ritardo + ", data_inizio=" + data_inizio + ", data_fine=" + data_fine
				+ "]";
	}

}
