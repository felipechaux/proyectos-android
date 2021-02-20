
//********************//
//Autor : Abel Felipe Chaux Gutierrez//
//********************//

public class TaskArrays {

	public static void main(String[] args) {

		// dimension
		byte dimen = 4;

		// matriz objetivo de dimension 4*4
		int[][] myArrayMatriz = new int[dimen][dimen];

		// a -> indice
		// b -> subindice

		// valor para asignar a matriz
		int value = 0;

		for (int a = 0; a < dimen; a++) {

			for (int b = 0; b < dimen; b++) {
				// incremento de value con postfijo ++
				value++;
				// operacion de multiplicacion para llenar matriz
				myArrayMatriz[a][b] = value * 2;
				// System.out.println("[I]:" + a + " ; [J]:" + b + " -> value : " + value * 2);
				System.out.print(myArrayMatriz[a][b] + " ");
			}
			System.out.println();

		}

	}

}
