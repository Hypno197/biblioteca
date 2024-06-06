package org.generation.italy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import org.generation.italy.model.Libri;
import org.generation.italy.model.Prestito;
import org.generation.italy.model.Utenti;

public class Sql {
	public static String url = "jdbc:mysql://localhost:3306/biblioteca?allowMultiQueries=true";
	public static DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	public static Libri l;
	public static String sql, numedit;

	public static void caricaLibri(ArrayList<Libri> elencoLibri) {
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			System.out.println("Connesso correttamente al DB");
			// inserire caricalibri
			String sql = "SELECT * FROM libri";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						Libri l = new Libri();
						l.id = rs.getInt("id");
						l.titolo = rs.getString("titolo");
						l.id_autore = rs.getInt("id_autore");
						l.id_editore = rs.getInt("id_editore");
						l.id_genere = rs.getInt("id_genere");
						l.num_pagine = rs.getInt("num_pagine");
						l.qnt = rs.getInt("qnt");
						l.data_pubblicazione = rs.getDate("data_pubblicazione").toLocalDate();
						elencoLibri.add(l);
					}
				}

			}
		} catch (Exception e) {
			System.err.println("Error : " + e.getMessage());
		}
	}

	public static int cercaAutore(String autore, Libri l) {
		int id_autore = 0;
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			Scanner sc = new Scanner(System.in);
			String sql = "SELECT id FROM autori WHERE CONCAT(nome, ' ', cognome) LIKE ?";
			try (PreparedStatement aut = conn.prepareStatement(sql)) {
				aut.setString(1, l.autore);
				try (ResultSet autrs = aut.executeQuery()) {
					if (!autrs.next()) {
						System.out.println("Autore non trovato!\nInserisci Nome dell'autore :");
						String nomeaut = sc.nextLine();
						System.out.println("Inserisci Cognome dell'autore :");
						String cognomeaut = sc.nextLine();
						System.out.println("Inserisci Data di Nascita gg/MM/aaaa:");
						LocalDate ddnaut = LocalDate.parse(sc.nextLine(), df);
						sql = "INSERT INTO autori(id, nome, cognome, data_nascita) VALUES (null, ?, ?, ?)";
						try (PreparedStatement addaut = conn.prepareStatement(sql)) {
							addaut.setString(1, nomeaut);
							addaut.setString(2, cognomeaut);
							addaut.setObject(3, ddnaut);
							addaut.executeUpdate();
						} catch (Exception e) {
							System.out.println("Errore 1 nel caricamento del nuovo libro : " + e.getMessage());
						}
						sql = "SELECT id FROM autori WHERE CONCAT(nome, ' ', cognome) LIKE ?";
						try (PreparedStatement aut2 = conn.prepareStatement(sql)) {
							aut2.setString(1, nomeaut + " " + cognomeaut);
							try (ResultSet aut2rs = aut2.executeQuery()) {
								while (aut2rs.next()) {
									id_autore = aut2rs.getInt("id");
								}
							}
						}
					} else
						id_autore = autrs.getInt("id");
				}

			}
		} catch (Exception e) {
			System.out.println("Errore 1 nel caricamento del nuovo libro : " + e.getMessage());
		}
		return id_autore;
	}

	public static int cercaGenere(String genere, Libri l) {
		int id_genere = 0;
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			String sql = "SELECT id FROM generi WHERE nome LIKE ?";
			try (PreparedStatement gen = conn.prepareStatement(sql)) {
				gen.setString(1, l.genere);
				try (ResultSet genrs = gen.executeQuery()) {
					if (!genrs.next()) {
						sql = "INSERT INTO generi(id, nome) VALUES (null, ?)";
						try (PreparedStatement addgen = conn.prepareStatement(sql)) {
							addgen.setString(1, l.genere);
							addgen.executeUpdate();
						}
						sql = "SELECT id FROM generi WHERE nome LIKE ?";
						try (PreparedStatement gen2 = conn.prepareStatement(sql)) {
							gen2.setString(1, l.genere);
							try (ResultSet genrs2 = gen2.executeQuery()) {
								while (genrs2.next()) {
									id_genere = genrs2.getInt("id");
								}
							}
						}

					} else
						id_genere = genrs.getInt("id");
				}
			}
		} catch (Exception e) {
			System.out.println("Errore 2 nel caricamento del nuovo libro : " + e.getMessage());
		}
		return id_genere;
	}

	public static int cercaEditore(String editore, Libri l) {
		Scanner sc = new Scanner(System.in);
		int id_editore = 0;
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			String sql = "SELECT id FROM editori WHERE nome LIKE ?";
			try (PreparedStatement edit = conn.prepareStatement(sql)) {
				edit.setString(1, l.editore);
				try (ResultSet editrs = edit.executeQuery()) {
					if (!editrs.next()) {
						System.out.println("Editore non trovato!\n Inserire Numero di Telefono :");
						numedit = sc.nextLine();
						sql = "INSERT INTO editori(id, nome, num_telefono) VALUES (null, ?, ?)";
						try (PreparedStatement addedit = conn.prepareStatement(sql)) {
							addedit.setString(1, l.editore);
							addedit.setString(2, numedit);
							addedit.executeUpdate();
						}
						sql = "SELECT id FROM editori WHERE nome LIKE ?";
						try (PreparedStatement edit2 = conn.prepareStatement(sql)) {
							edit2.setString(1, l.editore);
							try (ResultSet editrs2 = edit2.executeQuery()) {
								while (editrs2.next()) {
									id_editore = editrs2.getInt("id");
								}
							}
						}
					} else
						id_editore = editrs.getInt("id");
				}
			}
		} catch (Exception e) {
			System.out.println("Errore 3 nel caricamento del nuovo libro : " + e.getMessage());
		}
		return id_editore;
	}

	public static void caricaLibro(Libri l) {
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			sql = "INSERT INTO libri VALUES (null, ?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement inserisci = conn.prepareStatement(sql)) {
				inserisci.setString(1, l.titolo);
				inserisci.setInt(2, l.id_autore);
				inserisci.setInt(3, l.id_genere);
				inserisci.setObject(4, l.data_pubblicazione);
				inserisci.setInt(5, l.num_pagine);
				inserisci.setInt(6, 0);
				inserisci.setInt(7, l.id_editore);
				inserisci.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("Errore sql nel caricamento del nuovo libro : " + e.getMessage());
		}
	}

	public static void visualizzaLibri() {
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			System.out.println("Connesso correttamente al DB");
			sql = "SELECT * FROM libri INNER JOIN autori ON libri.id_autore=autori.id INNER JOIN editori ON libri.id_editore=editori.id INNER JOIN generi ON libri.id_genere=generi.id ";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						System.out.println("Codice Libro: " + rs.getInt("id"));
						System.out.println("Titolo: " + rs.getString("titolo"));
						System.out.println(
								"Autore: " + rs.getString("autori.nome") + " " + rs.getString("autori.cognome"));
						System.out.println("Editore: " + rs.getString("editori.nome"));
						System.out.println("Genere: " + rs.getString("generi.nome"));
						System.out.println("Data di Pubblicazione: " + rs.getDate("data_pubblicazione").toLocalDate());
						System.out.println(rs.getInt("num_pagine"));
						System.out.println(rs.getInt("qnt"));
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Errore : " + e.getMessage());
		}
	}

	public static void eliminaLibro() {
		Scanner sc = new Scanner(System.in);
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			System.out.println("Connesso correttamente al DB");
			System.out.println("Inserire libro da cancellare");
			int codcanc = sc.nextInt();
			sc.nextLine();
			sql = "DELETE FROM libri WHERE id = ?";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, codcanc);
				int librieliminati = ps.executeUpdate();
				System.out.println("Libri eliminati: " + librieliminati);
			}
		} catch (Exception e) {
			System.out.println("Errore : " + e.getMessage());
		}
	}

	public static Libri nuovoLibro() {
		Scanner sc = new Scanner(System.in);
		l = new Libri();
		System.out.println("Inserire Titolo");
		l.titolo = sc.nextLine();
		System.out.println("Inserire autore");
		l.autore = sc.nextLine();
		System.out.println("Inserire genere");
		l.genere = sc.nextLine();
		System.out.println("Inserire editore");
		l.editore = sc.nextLine();
		System.out.println("Inserire numero di pagine");
		l.num_pagine = sc.nextInt();
		sc.nextLine();
		System.out.println("Inserire data di pubblicazione gg/MM/aaaa");
		l.data_pubblicazione = LocalDate.parse(sc.nextLine(), df);
		return l;
	}

	public static void modificaLibro(int codmod, Libri l) {
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			sql = "UPDATE `libri` SET `titolo`=?,`id_autore`=?,`id_genere`=?,`data_pubblicazione`=?,`num_pagine`=?,`qnt`=?,`id_editore`=? WHERE id = ?";
			try (PreparedStatement modifica = conn.prepareStatement(sql)) {
				modifica.setString(1, l.titolo);
				modifica.setInt(2, l.id_autore);
				modifica.setInt(3, l.id_genere);
				modifica.setObject(4, l.data_pubblicazione);
				modifica.setInt(5, l.num_pagine);
				modifica.setInt(6, 0);
				modifica.setInt(7, l.id_editore);
				modifica.setInt(8, codmod);
				modifica.executeUpdate();
			}
		} catch (Exception e) {
			System.out.println("Errore 5 nella modifica del libro : " + e.getMessage());
		}
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
	}

	public static void nuovoUtente() {
		Scanner sc = new Scanner(System.in);
		Utenti user = new Utenti();
		System.out.println("Inserisci Nome dell'utente");
		user.nome = sc.nextLine();
		System.out.println("Inserisci Cognome dell'utente");
		user.cognome = sc.nextLine();
		System.out.println("Inserisci Numero di Telefono dell'utente");
		user.num_telefono = sc.nextLine();
		System.out.println("Inserisci data di nascita gg/MM/aaaa");
		user.data_nascita = LocalDate.parse(sc.nextLine(), df);
		System.out.println("Inserisci indirizzo");
		user.indirizzo = sc.nextLine();
		System.out.println("Inserisci Codice Fiscale");
		user.cod_fiscale = sc.nextLine();
		// verifica utente non registrato
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			sql = "SELECT * FROM utenti WHERE cod_fiscale = ?";
			try (PreparedStatement cercaCF = conn.prepareStatement(sql)) {
				cercaCF.setString(1, user.cod_fiscale);
				try (ResultSet rscf = cercaCF.executeQuery()) {
					if (!rscf.next()) { // se utente non presente aggiunge utente
						sql = "INSERT INTO `utenti` (`id`, `nome`, `cognome`, `data_nascita`, `num_telefono`, `indirizzo`, `cod_fiscale`) VALUES (NULL, ?, ?, ?, ?, ?, ?)";
						try (PreparedStatement addUser = conn.prepareStatement(sql)) {
							addUser.setString(1, user.nome);
							addUser.setString(2, user.cognome);
							addUser.setObject(3, user.data_nascita);
							addUser.setString(4, user.num_telefono);
							addUser.setString(5, user.indirizzo);
							addUser.setString(6, user.cod_fiscale);
							addUser.executeUpdate();

						}
						sql = "SELECT * FROM utenti WHERE cod_fiscale = ?";// recuper ID generato
						try (PreparedStatement idUser = conn.prepareStatement(sql)) {
							idUser.setString(1, user.cod_fiscale);
							try (ResultSet rsid = idUser.executeQuery()) {
								if (rsid.next())
									user.id = rsid.getInt("id");
							}
						}
						System.out.println("Utente codice: " + user.id + " aggiunto con successo!");

					} else
						System.out.println("Utente già registrato.");
				}
			}
		} catch (Exception e) {
			System.out.println("Errore nell'inserimento dell'utente1: " + e.getMessage());
		}
	}

	public static String RicercaUtente() {
		Scanner sc = new Scanner(System.in);
		Utenti user = new Utenti();
		String res = null;
		System.out.println("Inserisci il codice fiscale dell'utente");
		user.cod_fiscale = sc.nextLine();
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			sql = "SELECT * FROM utenti WHERE cod_fiscale = ?";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, user.cod_fiscale);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						user.id = rs.getInt("id");
						user.nome = rs.getString("nome");
						user.cognome = rs.getString("cognome");
						user.data_nascita = (rs.getDate("data_nascita")).toLocalDate();
						user.num_telefono = rs.getString("num_telefono");
						user.indirizzo = rs.getString("indirizzo");
						res = user.toString();
					} else
						res = "Utente non trovato!";
				}
			}
		} catch (Exception e) {
			System.out.println("Errore : " + e.getMessage());
		}

		return res;
	}

	public static void ModificaUtente() {
		Scanner sc = new Scanner(System.in);
		Utenti user = new Utenti();
		System.out.println("Inserisci il codice dell'utente da modificare: ");
		user.id = sc.nextInt();
		sc.nextLine();
		// verifica utente non registrato
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			sql = "SELECT * FROM utenti WHERE id = ?";
			try (PreparedStatement cercaID = conn.prepareStatement(sql)) {
				cercaID.setInt(1, user.id);
				try (ResultSet rscf = cercaID.executeQuery()) {
					if (rscf.next()) {// se utente registrato modifica utente
						System.out.println("Inserisci Nome dell'utente");
						user.nome = sc.nextLine();
						System.out.println("Inserisci Cognome dell'utente");
						user.cognome = sc.nextLine();
						System.out.println("Inserisci Numero di Telefono dell'utente");
						user.num_telefono = sc.nextLine();
						System.out.println("Inserisci data di nascita gg/MM/aaaa");
						user.data_nascita = LocalDate.parse(sc.nextLine(), df);
						System.out.println("Inserisci indirizzo");
						user.indirizzo = sc.nextLine();
						System.out.println("Inserisci Codice Fiscale");
						user.cod_fiscale = sc.nextLine();// se utente presente aggiorna utente
						sql = "UPDATE `utenti` SET nome=? , cognome=? , data_nascita=? , num_telefono=? , indirizzo=? , cod_fiscale=? WHERE id = ?";
						try (PreparedStatement updateUser = conn.prepareStatement(sql)) {
							updateUser.setString(1, user.nome);
							updateUser.setString(2, user.cognome);
							updateUser.setObject(3, user.data_nascita);
							updateUser.setString(4, user.num_telefono);
							updateUser.setString(5, user.indirizzo);
							updateUser.setString(6, user.cod_fiscale);
							updateUser.setInt(7, user.id);
							updateUser.executeUpdate();
							System.out.println("Utente Modificato.\n" + user.toString());
						}
					} else
						System.out.println("Utente non trovato.");
				}
			}
		} catch (Exception e) {
			System.out.println("Errore nell'aggiornamento dell'utente: " + e.getMessage());
		}
	}

	public static void NuovoPrestito() {
		Prestito p = new Prestito();
		Utenti user = new Utenti();
		Libri l = new Libri();
		Scanner sc = new Scanner(System.in);
		user = cercaUserDaCf();
		l = cercaLibroDaTitolo();
		System.out.println("Seleziona la quantità di libri richiesta: ");
		int qtapres = sc.nextInt();
		sc.nextLine();
		// validazione data
		boolean dataok = false;
		do {
			System.out.println("Inserire Data di fine prestito nel formato gg/MM/aaaa");
			try {
				p.data_fine = LocalDate.parse(sc.nextLine(), df);
				if (!p.data_fine.equals(null)) {
					dataok = true;
				}
			} catch (Exception e) {
				System.out.println("Data Non Valida!");
			}
		} while (!dataok);
		// carica prestito
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			for (int i = 0; i < qtapres; i++) {
				if (l.qnt > 0) {
					sql = "INSERT INTO `prestiti` (`id`, `id_utente`, `id_libro`, `data_inizio`, `data_fine`, `data_consegna`, `riconsegnato`, `ritardo`) VALUES (NULL, ? , ? , ? , ? , NULL, NULL, NULL) ; UPDATE libri SET qnt = qnt - 1 WHERE id = ? ";
					try (PreparedStatement aggpres = conn.prepareStatement(sql)) {
						aggpres.setInt(1, user.id);
						aggpres.setInt(2, l.id);
						aggpres.setObject(3, LocalDate.now());
						aggpres.setObject(4, p.data_fine);
						aggpres.setInt(5, l.id);
						aggpres.executeUpdate();
						l.qnt--;
					}
				} else {
					System.out.println("Libri terminati! Sono stati prestati " + i + " libri.");
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Errore nel caricamento del nuovo prestito : " + e.getMessage());
		}

	}

	public static String CercaDaID(String tabName, String colName, int ID) {
		String res = null;
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			sql = "SELECT " + colName + " FROM " + tabName + " WHERE id = " + ID;
			try (PreparedStatement cercadaid = conn.prepareStatement(sql)) {
				try (ResultSet cidrs = cercadaid.executeQuery()) {
					if (cidrs.next()) {
						res = cidrs.getString(colName.toString());
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Errore nell'aggiornamento dell'utente: " + e.getMessage());
		}
		return res;
	}

	public static Libri cercaLibroDaTitolo() {
		Libri l = new Libri();
		Scanner sc = new Scanner(System.in);
		boolean found = false;
		do {// ricerca libro
			System.out.println("Inserisci il titolo del libro da cercare:");
			l.titolo = sc.nextLine();
			try (Connection conn = DriverManager.getConnection(url, "root", "")) {
				sql = "SELECT * FROM libri WHERE titolo = ?";
				try (PreparedStatement cercaLibro = conn.prepareStatement(sql)) {
					cercaLibro.setString(1, l.titolo);
					try (ResultSet clrs = cercaLibro.executeQuery()) {
						if (clrs.next()) {// assegna attr libro a l
							found = true;
							l.id = clrs.getInt("id");
							l.titolo = clrs.getString("titolo");
							l.id_autore = clrs.getInt("id_autore");
							l.autore = CercaDaID("autori", "nome", l.id_autore);
							l.id_editore = clrs.getInt("id_editore");
							l.editore = CercaDaID("editori", "nome", l.id_editore);
							l.id_genere = clrs.getInt("id_genere");
							l.genere = CercaDaID("generi", "nome", l.id_genere);
							l.num_pagine = clrs.getInt("num_pagine");
							l.qnt = clrs.getInt("qnt");
							l.data_pubblicazione = clrs.getDate("data_pubblicazione").toLocalDate();
							System.out.println(l.toString());
						} else
							System.out.println("Libro Non Trovato!");
					}
				}
			} catch (Exception e) {
				System.out.println("Errore nell'aggiornamento dell'utente: " + e.getMessage());
			}
		} while (!found);
		return l;
	}

	public static Utenti cercaUserDaCf() {
		boolean found = false;
		Utenti user = new Utenti();
		Scanner sc = new Scanner(System.in);
		System.out.println("Inserisci il codice fiscale da cercare:");
		user.cod_fiscale = sc.nextLine();
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {
			do {
				sql = "SELECT * FROM utenti WHERE cod_fiscale = ?";
				try (PreparedStatement cercaCF = conn.prepareStatement(sql)) {
					cercaCF.setString(1, user.cod_fiscale);
					try (ResultSet rscf = cercaCF.executeQuery()) {
						if (!rscf.next()) {// se utente non registrato lo registra
							System.out.println("Utente non trovato! Aggiunta nuovo utente.");
							nuovoUtente();
						} else {
							System.out.println("Utente trovato");
							found = true;
							user.id = rscf.getInt("id");
							user.nome = rscf.getString("nome");
							user.cognome = rscf.getString("cognome");
							user.data_nascita = (rscf.getDate("data_nascita")).toLocalDate();
							user.num_telefono = rscf.getString("num_telefono");
							user.indirizzo = rscf.getString("indirizzo");
							System.out.println(user.toString());
						}
					}
				}
			} while (!found);
		} catch (Exception e) {
			System.out.println("Errore nella ricerca dell'utente: " + e.getMessage());
		}
		return user;
	}

	public static void Restituzione() {
		Prestito p = new Prestito();
		Utenti user = new Utenti();
		Libri l = new Libri();
		Scanner sc = new Scanner(System.in);
		user = cercaUserDaCf();
		l = cercaLibroDaTitolo();
		System.out.println("Seleziona la quantità di libri da restituire.");
		int qtarest = sc.nextInt();
		HashMap<Integer, LocalDate> libridarest=new HashMap<Integer, LocalDate>();
		sc.nextLine();
		try (Connection conn = DriverManager.getConnection(url, "root", "")) {//ciclo per restituire libri, check ritardi e scadenze, storage ID/data nell'hashmap, ci ripensiamo domani grazie ciao
				sql =	"SELECT * FROM prestiti WHERE id_utente = ? AND id_libro = ? AND riconsegnato = 0";
						try (PreparedStatement rit=conn.prepareStatement(sql)){
							rit.setInt(1, user.id);
							rit.setInt(2, l.id);
							try (ResultSet ritrs=rit.executeQuery()){
								if (ritrs.next()) {
									libridarest.put(ritrs.getInt("id"), (ritrs.getDate("data_fine")).toLocalDate());
								}
							}
			}						
						for (int i = 0; i < qtarest; i++) {
				sql = "UPDATE `prestiti` SET `riconsegnato` = '1', data_consegna= ? , ritardo = ?  WHERE id = ? AND prestiti.riconsegnato = 0 ; UPDATE libri SET qnt = qnt + 1 WHERE id = ?";
					try (PreparedStatement aggpres = conn.prepareStatement(sql)) {
					aggpres.setObject(1, LocalDate.now());
					
					// da finire
					}
					System.out.println("Libri restituiti! Sono stati ritornati " + i + " libri.");
			}
		} catch (Exception e) {
			System.out.println("Errore nel caricamento del nuovo prestito : " + e.getMessage());
		}
	}
}