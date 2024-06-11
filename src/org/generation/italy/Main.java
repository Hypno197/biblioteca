package org.generation.italy;

import java.util.ArrayList;
import java.util.Scanner;

import org.generation.italy.model.Libri;
import org.generation.italy.model.Utenti;

public class Main {
	public static Libri l;
	public static Utenti user;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ArrayList<Libri> elencoLibri = new ArrayList<Libri>();
		boolean esci = false, esci2=true;
		System.out.println("Benvenuto nel gestionale della biblioteca");
		do {
			esci2 = false;
			elencoLibri.clear();
			Sql.caricaLibri(elencoLibri);
			System.out.println(
					"Seleziona l'operazione da fare: \n1. Gestione Libri\n2. Gestione Utenti\n3. Gestione Prestiti");
			String scelta = sc.nextLine();
			switch (scelta) {
			case "1": {// gestione libri
				do {
					esci = false;
					System.out.println(
							"Scegli l'operazione da fare: \n1. Inserire Nuovo Libro \n2.Visualizza libri esistenti \n3.Cancellazione libro \n4. Modifica libro\n5. Esci");
					scelta = sc.nextLine();
					switch (scelta) {
					case "1": {
						l = Sql.nuovoLibro();
						l.id_autore = Sql.cercaAutore(l.autore, l);
						l.id_genere = Sql.cercaGenere(l.genere, l);
						l.id_editore = Sql.cercaEditore(l.editore, l);
						Sql.caricaLibro(l);
						System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
						break;
					}
					case "2": {
						Sql.visualizzaLibri();
						break;
					}
					case "3": {
						Sql.eliminaLibro();
						break;

					}
					case "4": {
						System.out.println("Connesso correttamente al DB");
						System.out.println("Inserire libro da modificare");
						int codmod = sc.nextInt();
						sc.nextLine();
						boolean libroEsiste = false;
						for (Libri i : elencoLibri) {
							if (i.id == codmod) {
								libroEsiste = true;
							}
						}
						if (!libroEsiste) {
							System.out.println(
									"Libro non trovato! Prova ad inserire il libro. Premi invio per continuare");
							sc.nextLine();
							continue;
						}
						sc.nextLine();
						l = Sql.nuovoLibro();
						l.id_autore = Sql.cercaAutore(l.autore, l);
						l.id_genere = Sql.cercaGenere(l.genere, l);
						l.id_editore = Sql.cercaEditore(l.editore, l);
						Sql.modificaLibro(codmod, l);
						break;
					}
					case "5":
						esci2 = true;
						break;
					default: {
						System.out.println("Selezione non valida!");
					}
					}
				} while (!esci);
				break;
			}
			case "2": { // gestione utenti
				do {
					esci = false;
					System.out.println(
							"Scegli l'operazione da fare \n1. Registra Nuovo Utente \n2. Ricerca Utente per Codice Fiscale\n3. Modifica Utente \n4. Esci");
					scelta = sc.nextLine();
					switch (scelta) {
					case "1": {
						Sql.nuovoUtente();
						break;
					}
					case "2": {
						System.out.println(Sql.RicercaUtente());
						break;
					}
					case "3": {
						Sql.ModificaUtente();
						break;
					}
					case "4": {
						esci = true;
						break;
					}
					default: {
						System.out.println("Selezione non valida!");
					}
					}
				} while (!esci);
				break;
			}
			case "3": {// gestione prestiti
				do {
					esci = false;
					System.out.println(
							"Scegli l'operazione da fare \n1. Registra Nuovo Prestito\n2. Restituzione libri\n3. Esci");
					scelta = sc.nextLine();
					switch (scelta) {
					case "1": {
						System.out.println("Nuovo prestito: ");
						Sql.NuovoPrestito();
						break;
					}
					case "2": {
						System.out.println("Restituzione: ");
						Sql.Restituzione();
						break;
					}
					case "3": {
						esci = true;
					}
					default:
						System.out.println("Selezione non valida!");
					}
				} while (!esci);
				break;
			}
			case "4": {
				do {
					esci = false;
					System.out.println("Gestione magazzino\n1. Scarico nuovi libri");// inserire scelte
					scelta = sc.nextLine();
					switch (scelta) {
					case "1": {
						System.out.println("Aggiunta dei libri");
						Sql.AggiungiLibri();
						break;
					}
					case "2": {
						System.out.println("Controllo giacenze");
						
						break;
					}
					default:
						System.out.println("Selezione non valida!");

					}

				} while (!esci);
				break;
			}
			default: {
				System.out.println("Selezione non valida!");
			}
			}
			
		} while (!esci2);
	}
}