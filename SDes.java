/**
 * Author: Hema Kothari
 */
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;

public class SDes {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//-----Data Variables
		int[] plainText = new int[8];
		int[] key = new int[10];
		int[] key1 = new int[8];
		int[] key2 = new int[8];
		int[] swap = new int[8];
		int[] decryptswap = new int[8];
		
		//----------Scanner to accept input from user
		Scanner input = new Scanner(System.in);
		
		//--------Accept Plain text from user
		System.out.println("Enter 8-bit block of Plain Text");
		String plainTextInput = input.nextLine();
		while(!plainTextInput.matches("[01]+") || plainTextInput.length()!=8){
			if(plainTextInput.length()!=8)
				System.out.println("Length of plain text should be 8 bits");
			else
				System.out.println("All should be 0 and 1");
			plainTextInput = input.nextLine();
		}	
		char[] ptChar = plainTextInput.toCharArray();
		for (int i = 0; i < ptChar.length; i++){
			plainText[i] = (int)(ptChar[i]-48);
		}
		
		//-------Accept Key from user
		System.out.println("Enter 10-bit Key");
		String keyInput = input.nextLine();
		while(!keyInput.matches("[01]+") || keyInput.length()!=10){
			if(keyInput.length()!=10)
				System.out.println("Length of key should be 10 bits");
			else
				System.out.println("All should be 0 and 1");
			keyInput = input.nextLine();
		}	
		char[] keyChar = keyInput.toCharArray();
		for (int i = 0; i < keyChar.length; i++){
			key[i] = (int)(keyChar[i]-48);
		}
		
		//-----Accept sbox from user to work on
		char sbox = ' ';
		do{
			System.out.println("Which S1 box you would like to use, type o/O for Original and m/M for modified");
			sbox = Character.toLowerCase(input.next().charAt(0));
		}while (sbox != 'o' && sbox != 'm');
		
		System.out.println();
		System.out.print("The entered 8-bit Plain text is: \t");
		for(int i=0; i<plainText.length; i++){
			System.out.print(plainText[i]);
		}
		
		System.out.println();
		System.out.print("The entered 10-bit Key is: \t");
		for(int i=0; i<key.length; i++){
			System.out.print(key[i]);
		}
		
		int[] P10 = permutaionTen(key);
		int[] P10left = new int[5];
		int[] P10right = new int[5];
		
		System.arraycopy(P10, 0, P10left, 0, P10left.length);
		System.arraycopy(P10, 5, P10right, 0, P10right.length);		
		
		//----Circular shift to generate Keys
		P10left = circularShift(P10left,1);
		P10right = circularShift(P10right,1);
		System.arraycopy(P10left, 0, P10, 0, P10left.length);
		System.arraycopy(P10right, 0, P10, P10left.length, P10right.length);
		key1 = permutationEight(P10);	//--Generated Key1
		
		System.arraycopy(P10, 0, P10left, 0, P10left.length);
		System.arraycopy(P10, 5, P10right, 0, P10right.length);	
		
		P10left = circularShift(P10left,2);
		P10right = circularShift(P10right,2);
		System.arraycopy(P10left, 0, P10, 0, P10left.length);
		System.arraycopy(P10right, 0, P10, P10left.length, P10right.length);
		key2 = permutationEight(P10);	//---Generated Key2
		
		////-------Encrption Process starts
		//-----Inverse the plain text
		int[] inverse = pInverse(plainText);
		
		//--Function call to calculate Fk
		int[] fk = functionF(inverse, key1, sbox);
		
		System.arraycopy(fk, 4, swap, 0, 3);
		System.arraycopy(fk, 0, swap, 4, 3);
		
		System.out.println();
		System.out.print("The intermediate result after the SW operation while encrypting: \t");
		for(int j=0 ;j<swap.length; j++){
			System.out.print(swap[j]);
		}
		
		int[] fk1 = functionF(swap, key2, sbox);
		System.out.println();
		
		int[] cipher = permutedIPInverse(fk1);
		System.out.print("The ciphertext is: \t");
		for(int j=0 ;j<cipher.length; j++){
			System.out.print(cipher[j]);
		}
		
		/////--------Decryption Process starts
		int[] decryptinverse = pInverse(cipher);
		
		int[] decryptfk = functionF(decryptinverse, key2, sbox);
		
		System.arraycopy(decryptfk, 4, decryptswap, 0, 3);
		System.arraycopy(decryptfk, 0, decryptswap, 4, 3);
		
		System.out.println();
		System.out.print("The intermediate result after the SW operation while decrypting: \t");
		for(int j=0 ;j<decryptswap.length; j++){
			System.out.print(decryptswap[j]);
		}
		
		int[] decryptfk1 = functionF(decryptswap, key1, sbox);
		System.out.println();
		int[] decryption = permutedIPInverse(decryptfk1);
		System.out.print("The result of decryption process is: \t");
		for(int j=0 ;j<decryption.length; j++){
			System.out.print(decryption[j]);
		}
	}
	
	//------Method to generate permutation 10
	public static int[] permutaionTen(int[] keyPerm){
		int[] result = new int[10];
		int[] combination = {2,4,1,6,3,9,0,8,7,5};
		for(int i=0; i<result.length; i++){
			result[i] = keyPerm[combination[i]];
		}
		
		return result;
	}
	
	//------Method to generate circular shift
	public static int[] circularShift(int[] perm10, int shift){
		int[] result = new int[5];
		System.arraycopy(perm10, shift, result, 0, (perm10.length-shift));
		System.arraycopy(perm10, 0, result, perm10.length-shift, shift);
		
		return result;
	}
	
	//------Method to generate permutation 8
	public static int[] permutationEight(int[] key1){
		int[] keyResult = new int[8];
		int[] combination = {5,2,6,3,7,4,9,8};
		for(int i=0; i<keyResult.length; i++){
			keyResult[i] = key1[combination[i]];
		}
		return keyResult;
	}
	
	//------Method to generate permutation 4
	public static int[] permutationFour(int[] temp){
		int[] temp1 = new int[4];
		int[] combination = {1,3,2,0};
		for(int i=0; i<temp.length; i++){
			temp1[i] = temp[combination[i]];
		}
		return temp1;
	}
	
	//------Method to generate permutated IP inverse
	public static int[] permutedIPInverse(int[] tempInv){
		int[] result = new int[8];
		int[] combination = {3, 0, 2, 4, 6, 1, 7, 5};
		for(int i=0; i<tempInv.length; i++){
			result[i] = tempInv[combination[i]];
		}
		return result;
	}
	
	//------Method to generate Fk
	public static int[] functionF(int[] plaintext, int[] orKey, char sbox){
		int[] result = new int[8];
		int[] left = new int[4];
		int[] right = new int[4];
		int[] rightTemp = new int[4];
		int[] S0look = new int[4];
		int[] S1look = new int[4];
		
		System.arraycopy(plaintext, 0, left, 0, left.length);
		System.arraycopy(plaintext, 4, right, 0, right.length);
		
		int[] expandR = expandRArray(right);
		int[] RxorKey = RXORKey(expandR, orKey);
		
		System.arraycopy(RxorKey, 0, S0look, 0, S0look.length);
		System.arraycopy(RxorKey, 4, S1look, 0, S1look.length);
		
		int[] s0return =  S0Lookup(S0look);
		int[] s1return = null;
		if(sbox == 'm'){
			s1return = modifiedS1Lookup(S1look);
		}else{
			s1return = S1Lookup(S1look);
		}
		
		System.arraycopy(s0return, 0, rightTemp, 0, s0return.length);
		System.arraycopy(s1return, 0, rightTemp, 2, s1return.length);
		
		rightTemp = permutationFour(rightTemp);
		left = RXORKey(rightTemp,left);
		
		System.arraycopy(left, 0, result, 0, left.length);
		System.arraycopy(right, 0, result, 4, right.length);			
		
		return result;
	}
	
	//------Method to generate P Inverse
	public static int[] pInverse(int[] ptext){
		int[] result = new int[8];
		int[] combination = {1,5,2,0,3,7,4,6};
		for(int i=0; i<result.length; i++){
			result[i] = ptext[combination[i]];
		}
		return result;
	}
	
	//------Method to expand right Array
	public static int[] expandRArray(int[] eRight){
		int[] result = new int[8];
		int[] combination = {3,0,1,2,1,2,3,0};
		for(int i=0; i<result.length; i++){
			result[i] = eRight[combination[i]];
		}
		return result;
	}
	
	//------Method to generate XOR
	public static int[] RXORKey(int[] Right, int[] keyxor){
		int[] result = new int[Right.length];
		for(int i=0; i<Right.length; i++){
			result[i] = Right[i]^keyxor[i];
		}
		return result;
	}
	
	//------Method for Original S0 Lookup
	public static int[] S0Lookup(int[] s0look){
		int[] result = new int[2];
		int[][] S0 = {{1, 0, 3, 2}, {3, 2, 1, 0}, {0, 2, 1, 3}, {3, 1, 3, 2}};
		int row = 0;
		int column = 0;
		
		if(s0look[0]==1 && s0look[3]==1)
			row = 3;
		else if(s0look[0]== 1 && s0look[3]==0)
			row = 2;
		else if(s0look[0]== 0 && s0look[3]==1)
			row = 1;
		else if(s0look[0]== 0 && s0look[3]==0)
			row = 0;
		
		if(s0look[1]==1 && s0look[2]==1)
			column = 3;
		else if(s0look[1]== 1 && s0look[2]==0)
			column = 2;
		else if(s0look[1]== 0 && s0look[2]==1)
			column = 1;
		else if(s0look[1]== 0 && s0look[2]==0)
			column = 0;
		
		int val = S0[row][column];
		if(val == 3){
			result[0] = 1; result[1] = 1;
		}else if(val == 2){
			result[0] = 1; result[1] = 0;
		}else if(val == 1){
			result[0] = 0; result[1] = 1;
		}else {
			result[0] = 0; result[1] = 0;
		}		
		
		return result;
	}
	
	//------Method for Original S1 Lookup
	public static int[] S1Lookup(int[] s1look){
		int[] result = new int[2];
		int[][] S1 = {{0, 1, 2, 3}, {2, 0, 1, 3}, {3, 0, 1, 0}, {2, 1, 0, 3}};
		int row = 0;
		int column = 0;
		
		if(s1look[0]==1 && s1look[3]==1)
			row = 3;
		else if(s1look[0]== 1 && s1look[3]==0)
			row = 2;
		else if(s1look[0]== 0 && s1look[3]==1)
			row = 1;
		else if(s1look[0]== 0 && s1look[3]==0)
			row = 0;
		
		if(s1look[1]==1 && s1look[2]==1)
			column = 3;
		else if(s1look[1]== 1 && s1look[2]==0)
			column = 2;
		else if(s1look[1]== 0 && s1look[2]==1)
			column = 1;
		else if(s1look[1]== 0 && s1look[2]==0)
			column = 0;
		
		int val = S1[row][column];
		if(val == 3){
			result[0] = 1; result[1] = 1;
		}else if(val == 2){
			result[0] = 1; result[1] = 0;
		}else if(val == 1){
			result[0] = 0; result[1] = 1;
		}else {
			result[0] = 0; result[1] = 0;
		}		
		
		return result;
	}
	
	//------Method for Modified S1 Lookup
	public static int[] modifiedS1Lookup(int[] s1look){
		int[] result = new int[2];
		int[][] S1 = {{0, 1, 2, 3}, {2, 1, 0, 3}, {3, 0, 1, 0}, {2, 0, 1, 3}};
		int row = 0;
		int column = 0;
		
		if(s1look[0]==1 && s1look[3]==1)
			row = 3;
		else if(s1look[0]== 1 && s1look[3]==0)
			row = 2;
		else if(s1look[0]== 0 && s1look[3]==1)
			row = 1;
		else if(s1look[0]== 0 && s1look[3]==0)
			row = 0;
		
		if(s1look[1]==1 && s1look[2]==1)
			column = 3;
		else if(s1look[1]== 1 && s1look[2]==0)
			column = 2;
		else if(s1look[1]== 0 && s1look[2]==1)
			column = 1;
		else if(s1look[1]== 0 && s1look[2]==0)
			column = 0;
		
		int val = S1[row][column];
		if(val == 3){
			result[0] = 1; result[1] = 1;
		}else if(val == 2){
			result[0] = 1; result[1] = 0;
		}else if(val == 1){
			result[0] = 0; result[1] = 1;
		}else {
			result[0] = 0; result[1] = 0;
		}		
		
		return result;
	}

}
