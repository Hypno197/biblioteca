package org.generation.italy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import org.generation.italy.libri.libri;

public class Main {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ArrayList<libri> elencoLibri = new ArrayList<libri>();
		String sql, nomeaut, cognomeaut, numedit;
		LocalDate ddnaut;
		libri l;
		boolean esci = false, check = false;
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String url = "jdbc:mysql://localhost:3306/biblioteca";
		System.out.println("Connessione in corso..");
		do {
			try (Connection conn = DriverManager.getConnection(url, "root", "")) {
				System.out.println("Connesso correttamente al DB");
				sql = "SELECT * FROM libri";
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					try (ResultSet rs = ps.executeQuery()) {
						while (rs.next()) {
							l = new libri();
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
			System.out.println(
					"Scegli l'operazione da fare: \n1. Inserire Nuovo Libro \n2.Visualizza libri esistenti \n3.Cancellazione libro \n4. Modifica libro");
			String scelta = sc.nextLine();
			switch (scelta) {
			case "1": {
				l = new libri();
				System.out.println("Inserire Titolo");
				l.titolo = sc.nextLine();
				System.out.println("Inserire autore");
				String autore = sc.nextLine();
				System.out.println("Inserire genere");
				String genere = sc.nextLine();
				System.out.println("Inserire editore");
				String editore = sc.nextLine();
				System.out.println("Inserire numero di pagine");
				l.num_pagine = sc.nextInt();
				sc.nextLine();
				System.out.println("Inserire data di pubblicazione gg/MM/aaaa");
				l.data_pubblicazione = LocalDate.parse(sc.nextLine(), df);
				// inizia traduzione da stringa a id
				try (Connection conn = DriverManager.getConnection(url, "root", "")) {
					check = false;
					sql = "SELECT id FROM autori WHERE CONCAT(nome, ' ', cognome) LIKE ?";
					try (PreparedStatement aut = conn.prepareStatement(sql)) {
						aut.setString(1, autore);
						try (ResultSet autrs = aut.executeQuery()) {
							while (!autrs.next()) {
								check = false;
								break;
							}
							if (!check)
								;
							{
								System.out.println("Autore non trovato!\nInserisci Nome dell'autore :");
								nomeaut = sc.nextLine();
								System.out.println("Inserisci Cognome dell'autore :");
								cognomeaut = sc.nextLine();
								System.out.println("Inserisci Data di Nascita gg/MM/aaaa:");
								ddnaut = LocalDate.parse(sc.nextLine(), df);
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
											l.id_autore = aut2rs.getInt("id");
										}
									}
								}
							}
							while (autrs.next()) {
								l.id_autore = autrs.getInt("id");
							}
						}
					} catch (Exception e) {
						System.out.println("Errore 1 nel caricamento del nuovo libro : " + e.getMessage());
					}
					check = true;
					sql = "SELECT id FROM generi WHERE nome LIKE ?";
					try (PreparedStatement gen = conn.prepareStatement(sql)) {
						gen.setString(1, genere);
						try (ResultSet genrs = gen.executeQuery()) {
							while (!genrs.next()) {
								check = false;
								break;
							}
							if (!check) {
								sql = "INSERT INTO generi(id, nome) VALUES (null, ?)";
								try (PreparedStatement addgen = conn.prepareStatement(sql)) {
									addgen.setString(1, genere);
									addgen.executeUpdate();
								}
								sql = "SELECT id FROM generi WHERE nome LIKE ?";
								try (PreparedStatement gen2 = conn.prepareStatement(sql)) {
									gen2.setString(1, genere);
									try (ResultSet genrs2 = gen2.executeQuery()) {
										while (genrs2.next()) {
											l.id_genere = genrs2.getInt("id");
										}
									}
								}

								while (genrs.next()) {
									l.id_genere = genrs.getInt("id");
								}
							}
						}
					} catch (Exception e) {
						System.out.println("Errore 2 nel caricamento del nuovo libro : " + e.getMessage());
					}
					check = true;
					sql = "SELECT id FROM editori WHERE nome LIKE ?";
					try (PreparedStatement edit = conn.prepareStatement(sql)) {
						edit.setString(1, editore);
						try (ResultSet editrs = edit.executeQuery()) {
							while (!editrs.next()) {
								check = false;
								break;
							}
							if (!check) {
								System.out.println("Editore non trovato!\n Inserire Numero di Telefono :");
								numedit = sc.nextLine();
								sql = "INSERT INTO editori(id, nome, num_telefono) VALUES (null, ?, ?)";
								try (PreparedStatement addedit = conn.prepareStatement(sql)) {
									addedit.setString(1, editore);
									addedit.setString(2, numedit);
									addedit.executeUpdate();
								}
								sql = "SELECT id FROM editori WHERE nome LIKE ?";
								try (PreparedStatement edit2 = conn.prepareStatement(sql)) {
									edit2.setString(1, editore);
									try (ResultSet editrs2 = edit2.executeQuery()) {
										while (editrs2.next()) {
											l.id_editore = editrs2.getInt("id");
										}
									}
								}
							}
							while (editrs.next()) {
								l.id_editore = editrs.getInt("id");
							}
						}
					} catch (Exception e) {
						System.out.println("Errore 3 nel caricamento del nuovo libro : " + e.getMessage());
					}
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
					} catch (Exception e) {
						System.out.println("Errore 4 nel caricamento del nuovo libro : " + e.getMessage());
					}
				} catch (SQLException e) {
					System.out.println("Errore sql nel caricamento del nuovo libro : " + e.getMessage());
				} catch (Exception e) {
					System.out.println("Errore 5 nel caricamento del nuovo libro : " + e.getMessage());
				}
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			case "2": {
				try (Connection conn = DriverManager.getConnection(url, "root", "")) {
					System.out.println("Connesso correttamente al DB");
					sql = "SELECT * FROM libri INNER JOIN autori ON libri.id_autore=autori.id INNER JOIN editori ON libri.id_editore=editori.id INNER JOIN generi ON libri.id_genere=generi.id ";
					try (PreparedStatement ps = conn.prepareStatement(sql)) {
						try (ResultSet rs = ps.executeQuery()) {
							while (rs.next()) {
								System.out.println("Codice Libro: " + rs.getInt("id"));
								System.out.println("Titolo: " + rs.getString("titolo"));
								System.out.println("Autore: " + rs.getString("autori.nome") + " "
										+ rs.getString("autori.cognome"));
								System.out.println("Editore: " + rs.getString("editori.nome"));
								System.out.println("Genere: " + rs.getString("generi.nome"));
								System.out.println(
										"Data di Pubblicazione: " + rs.getDate("data_pubblicazione").toLocalDate());
								System.out.println(rs.getInt("num_pagine"));
								System.out.println(rs.getInt("qnt"));
							}
						}
					}
				} catch (Exception e) {
					System.out.println("Errore : " + e.getMessage());
				}
				break;
			}
			case "3": {
				try (Connection conn = DriverManager.getConnection(url, "root", "")) {
					System.out.println("Connesso correttamente al DB");
					System.out.println("Inserire libro da cancellare");
					int codcanc = sc.nextInt();
					sc.nextLine();
					sql = "DELETE * FROM libri WHERE id == ?";
					try (PreparedStatement ps = conn.prepareStatement(sql)) {
						ps.setInt(1, codcanc);
						ps.executeUpdate();
					}
				} catch (Exception e) {
					System.out.println("Errore : " + e.getMessage());
				}
				break;

			}
			case "4":{
					System.out.println("Connesso correttamente al DB");
					System.out.println("Inserire libro da modificare");
					int codmod = sc.nextInt();
					sc.nextLine();
					l = new libri();
					System.out.println("Inserire Titolo");
					l.titolo = sc.nextLine();
					System.out.println("Inserire autore");
					String autore = sc.nextLine();
					System.out.println("Inserire genere");
					String genere = sc.nextLine();
					System.out.println("Inserire editore");
					String editore = sc.nextLine();
					System.out.println("Inserire numero di pagine");
					l.num_pagine = sc.nextInt();
					sc.nextLine();
					System.out.println("Inserire data di pubblicazione gg/MM/aaaa");
					l.data_pubblicazione = LocalDate.parse(sc.nextLine(), df);
					// inizia traduzione da stringa a id
					try (Connection conn = DriverManager.getConnection(url, "root", "")) {
						check = false;
						sql = "SELECT id FROM autori WHERE CONCAT(nome, ' ', cognome) LIKE ?";
						try (PreparedStatement aut = conn.prepareStatement(sql)) {
							aut.setString(1, autore);
							try (ResultSet autrs = aut.executeQuery()) {
								while (!autrs.next()) {
									check = false;
									break;
								}
								if (!check)
									;
								{
									System.out.println("Autore non trovato!\nInserisci Nome dell'autore :");
									nomeaut = sc.nextLine();
									System.out.println("Inserisci Cognome dell'autore :");
									cognomeaut = sc.nextLine();
									System.out.println("Inserisci Data di Nascita gg/MM/aaaa:");
									ddnaut = LocalDate.parse(sc.nextLine(), df);
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
												l.id_autore = aut2rs.getInt("id");
											}
										}
									}
								}
								while (autrs.next()) {
									l.id_autore = autrs.getInt("id");
								}
							}
						} catch (Exception e) {
							System.out.println("Errore 1 nel caricamento del nuovo libro : " + e.getMessage());
						}
						check = true;
						sql = "SELECT id FROM generi WHERE nome LIKE ?";
						try (PreparedStatement gen = conn.prepareStatement(sql)) {
							gen.setString(1, genere);
							try (ResultSet genrs = gen.executeQuery()) {
								while (!genrs.next()) {
									check = false;
									break;
								}
								if (!check) {
									sql = "INSERT INTO generi(id, nome) VALUES (null, ?)";
									try (PreparedStatement addgen = conn.prepareStatement(sql)) {
										addgen.setString(1, genere);
										addgen.executeUpdate();
									}
									sql = "SELECT id FROM generi WHERE nome LIKE ?";
									try (PreparedStatement gen2 = conn.prepareStatement(sql)) {
										gen2.setString(1, genere);
										try (ResultSet genrs2 = gen2.executeQuery()) {
											while (genrs2.next()) {
												l.id_genere = genrs2.getInt("id");
											}
										}
									}

									while (genrs.next()) {
										l.id_genere = genrs.getInt("id");
									}
								}
							}
						} catch (Exception e) {
							System.out.println("Errore 2 nella modifica del libro: " + e.getMessage());
						}
						check = true;
						sql = "SELECT id FROM editori WHERE nome LIKE ?";
						try (PreparedStatement edit = conn.prepareStatement(sql)) {
							edit.setString(1, editore);
							try (ResultSet editrs = edit.executeQuery()) {
								while (!editrs.next()) {
									check = false;
									break;
								}
								if (!check) {
									System.out.println("Editore non trovato!\n Inserire Numero di Telefono :");
									numedit = sc.nextLine();
									sql = "INSERT INTO editori(id, nome, num_telefono) VALUES (null, ?, ?)";
									try (PreparedStatement addedit = conn.prepareStatement(sql)) {
										addedit.setString(1, editore);
										addedit.setString(2, numedit);
										addedit.executeUpdate();
									}
									sql = "SELECT id FROM editori WHERE nome LIKE ?";
									try (PreparedStatement edit2 = conn.prepareStatement(sql)) {
										edit2.setString(1, editore);
										try (ResultSet editrs2 = edit2.executeQuery()) {
											while (editrs2.next()) {
												l.id_editore = editrs2.getInt("id");
											}
										}
									}
								}
								while (editrs.next()) {
									l.id_editore = editrs.getInt("id");
								}
							}
						} catch (Exception e) {
							System.out.println("Errore 3 nella modifica del libro : " + e.getMessage());
						}
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
						} catch (Exception e) {
							System.out.println("Errore 4 nella modifica del libro : " + e.getMessage());
						}
					} catch (SQLException e) {
						System.out.println("Errore sql nella modifica del libro: " + e.getMessage());
					} catch (Exception e) {
						System.out.println("Errore 5 nella modifica del libro : " + e.getMessage());
					}
					System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
					break;
			}
			default : {
					System.out.println("Selezione non valida!");
			}
			}
		} while (!esci);
		sc.close();
	}
}
