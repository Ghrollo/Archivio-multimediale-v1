package v1package;

import java.time.LocalDate;

import myutil.BelleStringhe;
import myutil.InputDati;
import myutil.MyMenu;

public class Main {
	
	private final static String ESCI = "ESCI";
	private final static String INDIETRO = "INDIETRO";
	private final static String RIPROVA_OR_ESCI = "\nRiprova o digita "+ESCI+" per tornare indietro";
	private final static int MAX_NICKNAME = 12;
	private final static int MIN_NICKNAME = 4;
	private final static int MIN_PSW = 4;
	private final static int MAX_PSW = 10;
	private final static String INSERIRE_NICKNAME = "Inserire un nickname, compreso tra "+ MIN_NICKNAME + " e " + MAX_NICKNAME + "caratteri, esclusi caratteri speciali (ex: ?!$%) :";
	private final static String INSERIRE_PSW = "inserire una password per l'account, da un minimo di "+ MIN_PSW+ " caratteri ad un massimo di " + MAX_PSW + " :";
	private final static String INSERIRE_NOME = "Inserire il vostro nome :";
	private final static String INSERIRE_COGNOME = "Inserire il vostro cognome :";
	private final static String INSERIRE_RESIDENZA = "Inserire l'indirizzo della vostra residenza";
	private final static String MODIFICA_PSW = "MODIFICA LA TUA PASSWORD";
	private final static String VISUALIZZA_SCHEDA_UTENTE = "VISUALIZZA LA SCHEDA UTENTE";
	private final static String RICHIESTA_RINNOVO = "RICHIEDI IL RINNOVO DELLA TUA ISCRIZIONE";
	private final static String CANCELLA_ACCOUNT = "CANCELLA IL TUO ACCOUNT";
	private final static String VISUALIZZA_FRUITORI = "VISUALIZZA TUTTI I FRUITORI";
	private final static String VISUALIZZA_RINNOVABILI = "VISUALIZZA I SOLI FRUITORI IN STATO DI RINNOVABILITA'";
	private final static String VISUALIZZA_DECADUTI = "VISUALIZZA GLI  EX-FRUITORI DELL'ARCHIVIO, I DECADUTI";
	private final static String INSERIRE_DATA_DI_NASCITA = "Inserire la vostra data di nascità (V.M. 18)";
	private final static String LOGIN_OPERATORE = "Login Operatore..";
	private final static String MSG_CANCELLAZIONE_ACCOUNT = "Cancellazione account in corso..";
	private final static String MSG_OMONIMIA = "Attenzione! questo nome è già stato occupato da un altro utente!";
	private final static String MSG_INESISTENZA_UTENTE = "Non esiste alcun utente con il nome %s." + RIPROVA_OR_ESCI;
	private final static String MSG_PSW_ERRATA = "la password inserita è errata."+RIPROVA_OR_ESCI;
	private final static String CMD_MODE_UTENTE = "Accedi come Utente";
	private final static String CMD_MODE_ADMIN = "Accedi come Amministratore";
	
	
	public static void main(String[] args) 
	{
	
		ArchivioFruitori archivio = new ArchivioFruitori();
		archivio.rimozioneDecaduti();
		 boolean fine = false;
		do
		{
		int comando = MyMenu.sceltaMultipla("REGISTRATI","ACCEDI",ESCI);
		switch(comando)
		{
		case 1:
			Fruitore fruitore = registrazioneFruitore(archivio);
			archivio.aggiungiFruitore(fruitore);
			menuUtente(fruitore,archivio);
			break;
		
		case 2:
			menuAccedi(archivio);
			break;
		
		case 3:
			fine = true;
			break;
		
		}
		}
		while(!fine);

	}
	
	private static Fruitore registrazioneFruitore(ArchivioFruitori archivio)
	{
		Fruitore fruitore;
		boolean omonimia = true;
		String nickname;
		do
		{
		nickname = InputDati.leggiNickname(INSERIRE_NICKNAME, MIN_NICKNAME, MAX_NICKNAME);
		if(archivio.isOmonimo(nickname))
			System.out.println(MSG_OMONIMIA);
		else 
			omonimia = false;
		}
		while (omonimia);
		String psw = InputDati.leggiStringaCompresaTra(INSERIRE_PSW, MIN_PSW, MAX_PSW);
		String nome = InputDati.leggiStringa(INSERIRE_NOME);
		String cognome = InputDati.leggiStringa(INSERIRE_COGNOME);
		String residenza = InputDati.leggiStringa(INSERIRE_RESIDENZA);
		LocalDate dataDiNascita = InputDati.soloMaggiorenni(INSERIRE_DATA_DI_NASCITA);
		fruitore = new Fruitore(nickname,psw,nome,cognome,residenza,dataDiNascita);
				
		return fruitore;
		
	}

	private static void menuAccedi(ArchivioFruitori archivio)
	{
		boolean termina = false;
		do
		{
			int comando = MyMenu.sceltaMultipla(CMD_MODE_UTENTE,CMD_MODE_ADMIN,INDIETRO);
			switch(comando)
			{
			case 1:
				String nickname;
				boolean esistenza = false;
				do
				{
					nickname = InputDati.leggiNickname(INSERIRE_NICKNAME, MIN_NICKNAME, MAX_NICKNAME);
					if(nickname.toUpperCase().equals(ESCI))
						return;
					if(!archivio.isOmonimo(nickname))
						System.out.println(MSG_INESISTENZA_UTENTE);
					else 
						esistenza = true;
				}
				while(!esistenza);
				Fruitore fruitore = archivio.getFruitore(nickname);
				if(checkPassword(fruitore))
					menuUtente(fruitore,archivio);
				break;
		
			case 2:
				System.out.println(BelleStringhe.centraIncornica(LOGIN_OPERATORE));
				if(checkPassword(archivio))
					menuGestore(archivio);
				break;
		
			case 3:
				termina = true;
				break;
			
			}
		}
		while(!termina);
	}
	
	private static void menuUtente(Fruitore fruitore, ArchivioFruitori archivio)
	{
		
		boolean termine = false;
		do
		{
		int comando = MyMenu.sceltaMultipla(VISUALIZZA_SCHEDA_UTENTE, MODIFICA_PSW, RICHIESTA_RINNOVO, CANCELLA_ACCOUNT, INDIETRO);
		switch(comando)
		{
		case 1:
			System.out.println(fruitore.descrizione());
			break;
			
		case 2:
			String psw;
			if(checkPassword(fruitore))
			{
				psw = InputDati.leggiStringaCompresaTra(INSERIRE_PSW, MIN_PSW, MAX_PSW);
				fruitore.setPassword(psw);
			}
			break;
		
		case 3:
			fruitore.richiestaRinnovo();
			break;
		
		case 4:
			System.out.println(BelleStringhe.incornicia(MSG_CANCELLAZIONE_ACCOUNT));
			if(checkPassword(fruitore))
				{
				archivio.rimozioneFruitore(fruitore);
				termine = true;
				}
			break;
		
		case 5:
			termine = true;
			break;
		}
		}
		while(!termine);
	}
	
	private static void menuGestore(ArchivioFruitori archivio)
	{
	
		boolean termina = false;
		do
		{
			int comando = MyMenu.sceltaMultipla(VISUALIZZA_FRUITORI, VISUALIZZA_RINNOVABILI, VISUALIZZA_DECADUTI, MODIFICA_PSW, INDIETRO);
			switch(comando)
			{
			
			case 1:
				archivio.stampaFruitori();
				break;
			
			case 2:
				archivio.stampaRinnovabili();
				break;
			
			case 3:
				archivio.stampaDecaduti();
				break;
			
			case 4:
				String psw;
				if(checkPassword(archivio))
				{
					psw = InputDati.leggiStringaCompresaTra(INSERIRE_PSW, MIN_PSW, MAX_PSW);
					archivio.setPassword(psw);
				}
				break;
			
			case 5:
				termina = true;
				break;
			}
		}
		while(!termina);
	}
	
	private static boolean checkPassword(Fruitore fruitore)
	{
		String psw;
		do
		{
			psw = InputDati.leggiStringaCompresaTra(INSERIRE_PSW, MIN_PSW, MAX_PSW);
			if(psw.toUpperCase().equals(ESCI))
				return false;
			if(!fruitore.checkPassword(psw))
				System.out.println(MSG_PSW_ERRATA);
		}
		while(!fruitore.checkPassword(psw));
		return true;
	}
	
	private static boolean checkPassword(ArchivioFruitori archivio)
	{
		String psw;
		do
		{
			psw = InputDati.leggiStringaCompresaTra(INSERIRE_PSW, MIN_PSW, MAX_PSW);
			if(psw.toUpperCase().equals(ESCI))
				return false;
			if(!archivio.checkPassword(psw))
				System.out.println(MSG_PSW_ERRATA);
		}
		while(!archivio.checkPassword(psw));
		return true;
	}
}
