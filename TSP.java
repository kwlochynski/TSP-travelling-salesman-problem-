import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;

public class TSP {

	public static void main(String[] args) throws FileNotFoundException {
		int size = 0;
		int ilosc = 1000;
		int turnieje = 13;

		Integer tablicaMiast[][] = null;
		File file = new File("sciezka_do_pliku.txt");
		try {
			Scanner skaner = new Scanner(file);
			size = skaner.nextInt();
			tablicaMiast = new Integer[size][size];

			while (skaner.hasNextInt()) {

				for (int i = 0; i < size; i++) {
					for (int j = 0; j <= i; j++) {
						tablicaMiast[i][j] = skaner.nextInt();
						tablicaMiast[j][i] = tablicaMiast[i][j];
					}
				}
			}
			skaner.close();

		} catch (FileNotFoundException e) {
			System.out.println("Plik nie istnieje!");
		}

		int populacja = size;
		List<Integer> lista = new ArrayList<Integer>();
		for (int i = 0; i < populacja; i++) {
			lista.add(i);
		}

		Integer kolejnoscMiast[][] = new Integer[populacja][size];

		for (int i = 0; i < populacja; i++) {
			Collections.shuffle(lista);
			for (int j = 0; j < size; j++) {
				kolejnoscMiast[i][j] = lista.get(j);
			}
		}

		Integer odleglosci[] = ObliczOdleglosci(size, tablicaMiast, kolejnoscMiast);

		Integer populacjaRodzicielska[][] = new Integer[odleglosci.length][2];
		for (int i = 0; i < populacjaRodzicielska.length; i++) {
			populacjaRodzicielska[i][0] = turniejowa(odleglosci, turnieje)[0];
			populacjaRodzicielska[i][1] = turniejowa(odleglosci, turnieje)[1];
		}

		Integer[][] potomkowie = krzyzowanie(populacjaRodzicielska, kolejnoscMiast);

		for (int x = 0; x < ilosc; x++) {
			odleglosci = ObliczOdleglosci(size - 1, tablicaMiast, potomkowie);

			for (int i = 0; i < populacjaRodzicielska.length; i++) {
				populacjaRodzicielska[i][0] = turniejowa(odleglosci, turnieje)[0];
				populacjaRodzicielska[i][1] = turniejowa(odleglosci, turnieje)[1];

			}

			potomkowie = krzyzowanie(populacjaRodzicielska, potomkowie);
			potomkowie = mutacja(potomkowie);
		}

		int min = Integer.MAX_VALUE;
		int najlepszaTrasa = 0;
		odleglosci = ObliczOdleglosci(potomkowie.length - 1, tablicaMiast, potomkowie);
		for (int i = 0; i < size - 1; i++) {

			if (min > odleglosci[i]) {
				min = odleglosci[i];
				najlepszaTrasa = i;
			}
		}

		for (int i = 0; i < potomkowie.length; i++) {
			if (i < potomkowie.length - 1)
				System.out.print(potomkowie[najlepszaTrasa][i] + "-");
			else
				System.out.print(potomkowie[najlepszaTrasa][i] + " ");
		}

		int tmpdistance = 0;

		for (int i = 0; i < size; i++) {
			if (i < size-1)
				tmpdistance = tmpdistance
						+ tablicaMiast[potomkowie[najlepszaTrasa][i]][potomkowie[najlepszaTrasa][i + 1]];
			else
				tmpdistance = tmpdistance + tablicaMiast[potomkowie[najlepszaTrasa][i]][potomkowie[najlepszaTrasa][0]];
		}

		// System.out.println("NAJLEPSZA ODLEGLOSC");
		System.out.println(tmpdistance);
	}

	public static Integer[][] krzyzowanie(Integer[][] populacjaRodzicielska, Integer kolejnoscMiast[][]) {
		Integer[][] potomkowie = new Integer[populacjaRodzicielska.length][populacjaRodzicielska.length];
		for (int i = 0; i < populacjaRodzicielska.length - 1; i++) {

			Random r = new Random();
			int punktStartowy = r.nextInt(populacjaRodzicielska.length - 1);
			int punktKoncowy;
			do {
				punktKoncowy = r.nextInt(populacjaRodzicielska.length);
			} while (punktKoncowy < punktStartowy);

			for (int x = punktStartowy; x <= punktKoncowy; x++) {
				potomkowie[i][x] = kolejnoscMiast[populacjaRodzicielska[i][1]][x];
			}

			for (int x = 0; x < populacjaRodzicielska.length; x++) {
				Integer tmp = kolejnoscMiast[populacjaRodzicielska[i + 1][1]][x];
				if (!Arrays.asList(potomkowie[i]).contains(tmp)) {
					for (int y = 0; y < populacjaRodzicielska.length; y++) {
						if (potomkowie[i][y] == null) {
							potomkowie[i][y] = kolejnoscMiast[populacjaRodzicielska[i + 1][1]][x];
							break;
						}
					}
				} else {
					for (int y = 0; y < populacjaRodzicielska.length; y++) {
						if (potomkowie[i][y] == null) {
							// potomkowie[i][y] = kolejnoscMiast[populacjaRodzicielska[i +
							// 1][1]][potomkowie[i][kolejnoscMiast[populacjaRodzicielska[i + 1][1]][x]]];
							break;
						}
					}
				}
			}
		}
		return potomkowie;
	}

	public static Integer[][] mutacja(Integer[][] potomkowie) {
		Random r = new Random();
		Integer punktStartowy;
		Integer punktKoncowy;
		int tmp;
		for (Integer i = 0; i < potomkowie.length - 1; i++) {
			punktStartowy = r.nextInt(potomkowie.length - 2);
			do {
				punktKoncowy = r.nextInt(potomkowie.length - 1);
			} while (punktKoncowy < punktStartowy);

			for (Integer j = punktStartowy; j <= punktKoncowy; j++) {
				tmp = potomkowie[i][j];
				potomkowie[i][j] = potomkowie[i][punktKoncowy];
				potomkowie[i][punktKoncowy] = tmp;
				punktKoncowy--;
			}
		}
		return potomkowie;
	}

	public static Integer[] turniejowa(Integer[] odleglosci, int rozmiarTurnieju) {
		Random r = new Random();
		int osobnik;
		Integer wynik[] = new Integer[2];
		wynik[0] = Integer.MAX_VALUE;
		int random;
		for (int i = 0; i < rozmiarTurnieju; i++) {
			random = r.nextInt(odleglosci.length);
			osobnik = odleglosci[random];

			if (wynik[0] > osobnik) {
				wynik[0] = osobnik;
				wynik[1] = random;
			}
		}
		return wynik;
	}

	public static Integer[] ObliczOdleglosci(int size, Integer tablicaMiast[][], Integer kolejnoscMiast[][]) {
		Integer odleglosci[] = new Integer[size];
		for (int i = 0; i < size; i++) {
			odleglosci[i] = 0;
			for (int j = 0; j < size - 1; j++) {
				odleglosci[i] += tablicaMiast[kolejnoscMiast[i][j]][kolejnoscMiast[i][j + 1]];
			}
			odleglosci[i] += tablicaMiast[kolejnoscMiast[i][size - 1]][kolejnoscMiast[i][0]];
		}
		return odleglosci;
	}

	public static int[] ruletka(int[] odleglosci) {
		int[] a = new int[odleglosci.length];
		int[] ruletka = new int[odleglosci.length];
		int suma = 0;
		int znajdz = 0;
		int losowa = 0;
		int max;

		Arrays.sort(odleglosci); //sortowanie od najmniejszej
		max = odleglosci[odleglosci.length-1];

		for (int i = 0; i < odleglosci.length; i++) {
			a[i] = max + 1 - odleglosci[i];
		}
		for (int i = 0; i < odleglosci.length; i++) {
				suma += a[i];
		}

		Random rnd = new Random();
		int y;
		for(int x=0; x<odleglosci.length; x++) {
			y=0;
			znajdz=0;
			losowa = rnd.nextInt(suma);

			while(znajdz<losowa) {
				znajdz += a[y];
				y++;
			}
			ruletka[x] = a[y];
		}
		return ruletka;
	}
}
