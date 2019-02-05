package assignment1;

public class Message {
	
	public String message;
	public int lengthOfMessage;

	public Message (String m){
		message = m;
		lengthOfMessage = m.length();
		this.makeValid();
	}
	
	public Message (String m, boolean b){
		message = m;
		lengthOfMessage = m.length();
	}
	
	/**
	 * makeValid modifies message to remove any character that is not a letter and turn Upper Case into Lower Case
	 */
	public void makeValid(){
		String valid = "";
		for(int i=0; i<lengthOfMessage; i++) {
			char check = message.charAt(i);                 // this will help check each character in the message one by one
			if(check>='A' && check<='Z') check+=32;         // firstly, decapitalize if a letter is capital
			if(check>='a' && check<='z') valid+=check;      // now that all "letters" are between a-z, eliminate the non-letters by ONLY adding the letters to the new valid Stirng,
			else continue;                                  // and skipping the non-letters
		}
		lengthOfMessage = valid.length();
		message = valid;
	}
	
	/**
	 * prints the string message
	 */
	public void print(){
		System.out.println(message);
	}
	
	/**
	 * tests if two Messages are equal
	 */
	public boolean equals(Message m){
		if (message.equals(m.message) && lengthOfMessage == m.lengthOfMessage){
			return true;
		}
		return false;
	}
	
	/**
	 * caesarCipher implements the Caesar cipher : it shifts all letter by the number 'key' given as a parameter.
	 * @param key
	 */
	public void caesarCipher(int key){
		String ciphered = "";
		for(int i=0; i<lengthOfMessage; i++) {
			char letter = message.charAt(i);
			int difference = (letter+key-'a')%26;              // this formula will protect the letter in case the key is bigger than 26, it calculates the new difference from 'a'
			if(difference>=0) letter = (char)('a'+difference); // if the difference is +ive or 0, we simply add it to 'a'
			if(difference<0) letter = (char)(123+difference);  // if its -ive (only possible with -ive keys) we need to go back to 'z', so its subtracted from 123 which is '{', the char after 'z'
			
			ciphered+=letter;
		}
		message = ciphered;
	}
	
	public void caesarDecipher(int key){
		this.caesarCipher(- key);
	}
	
	/**
	 * caesarAnalysis breaks the Caesar cipher
	 * you will implement the following algorithm :
	 * - compute how often each letter appear in the message
	 * - compute a shift (key) such that the letter that happens the most was originally an 'e'
	 * - decipher the message using the key you have just computed
	 */
	public void caesarAnalysis(){
		// find which char is used the most (in case of equality, the first one found will be taken)
		int count = 0;           // will store the count of the selected char in each loop
		int mostUsedCount = -1;  // will store the number of times the most used char was used
		char mostUsed = 0;       // will store the most used char
		for(int i=0; i<lengthOfMessage; i++) {
			char toCount = message.charAt(i);
			for(int j=0; j<lengthOfMessage; j++) {
				char toCompare = message.charAt(j);
				if(toCompare == toCount) count++;
			}
			if(count>mostUsedCount) {
				mostUsedCount = count;
				mostUsed = toCount;
			}
			count = 0;
		}
		// find the key to make it 'e' and decipher the code with that key
		int key = mostUsed-'e';
		caesarDecipher(key);
	}
	
	/**
	 * vigenereCipher implements the Vigenere Cipher : it shifts all letter from message by the corresponding shift in the 'key'
	 * @param key
	 */
	public void vigenereCipher (int[] key){
		String originalMessage = message;  // i need to store this here because i'll disturb the message attribute within the for loop to be able to use non-static method caesarCipher
		String ciphered = "";
		for(int i=0; i<originalMessage.length(); i++) {
			message = ""+originalMessage.charAt(i);
			lengthOfMessage = 1;             // this also needs to change to be able to use the caesarCipher method effectively without causing stringindexOutOfBoundsException
			int keyIndex = i%key.length;
			this.caesarCipher(key[keyIndex]);
			ciphered += message;
		}
		message = ciphered;
		lengthOfMessage = ciphered.length();
	}

	/**
	 * vigenereDecipher deciphers the message given the 'key' according to the Vigenere Cipher
	 * @param key
	 */
	public void vigenereDecipher (int[] key){
		/* like the way caesarDecipher(key) uses caesarCipher(-key), 
		 * i'm multiplying all the keys in my array with -1 and use this new negative version key array, 
		 * instead of copying all vigenereCipher method and changing caesarCipher lines to caesarDecipher
		 */
		for(int i=0; i<key.length; i++) {
			key[i] = -key[i];
		}
		vigenereCipher(key);
	}
	
	/**
	 * transpositionCipher performs the transition cipher on the message by reorganizing the letters and eventually adding characters
	 * @param key
	 */
	public void transpositionCipher (int key){
		// create the matrix, row first ([0][0] [0][1] [0][2]...)
		int originalLength = lengthOfMessage;
		while(lengthOfMessage%key != 0) lengthOfMessage++; // this will be the length of the message with asterisks added to make a whole rectangle
		int row = lengthOfMessage/key;                     // the key gives the column count, and now row gives the row count
		char[][]matrix = new char[row][key];
		int index = 0;
		for(int i=0; i<row; i++) {
			for(int j=0; j<key; j++) {
				if(index < originalLength) {                   // as long as the index is within the original message the letters will be copied
					matrix[i][j] = message.charAt(index);
					index++;
				} else matrix[i][j] = '*';                     // rest will be filled with asterisks
			}
		}
		// read the matrix, column first ([0][0] [1][0] [2][0]...)
		String ciphered = "";
		for(int j=0; j<key; j++) {
			for(int i=0; i<row; i++) {
				ciphered += matrix[i][j];
			}
		}
		message = ciphered;
	}
	
	/**
	 * transpositionDecipher deciphers the message given the 'key'  according to the transition cipher.
	 * @param key
	 */
	public void transpositionDecipher (int key){
		// create the matrix, column first
		int row = lengthOfMessage/key;
		char[][]matrix = new char[row][key];
		int index = 0;
		while(index<lengthOfMessage) {
			for(int j=0; j<key; j++) {
				for(int i=0; i<row; i++) {
					matrix[i][j] = message.charAt(index);
					index++;
				}
			}
		}
		// read the matrix, row first
		String original = "";
		for(int i=0; i<row; i++) {
			for(int j=0; j<key; j++) {
				char toAdd = matrix[i][j];
				if(toAdd!='*')	original += toAdd;
			}
		}
		message = original;
		lengthOfMessage = original.length();
	}
	
}