import java.util.ArrayList;


public class Arithmetic {

	// ������Ɋ܂܂�銿�����𐔎��ɂ��郁�\�b�h
	// FIXME: ���ʂ����鏈�������܂������Ȃ����Ƃ������B����傫�ȏC����v����\��������B
	public static String kanjiToNumeral(String formula) {
		String digit = "���O�l�ܘZ������";
		String unit = "�\�S��";
		String bigUnit = "������";

		// ��X�̌v�Z�̂��߂Ɂu���v�u���v�u���v�P�ʂɊ��ʂ�����F�\�񖜌ܕS�� -> (�\��)��(�ܕS��)
		StringBuilder sb = new StringBuilder(); // formula���R�s�[���ĐF�X�����邽�߂�StringBuilder
		sb.append(formula);
		int prev=0, dis=0; // �O�̕����ƍ��̕����̑����B���Z�q�Ȃǐ����ȊO:0, ��ȉ��̊������܂��͎Z�p����:1, ���ȏ�̊�����:2
		
		for (int i=0; i<sb.length(); i++) {
			if (	sb.charAt(i)=='*'
					|| sb.charAt(i)=='/' || sb.charAt(i)=='��'
					|| sb.charAt(i)=='+'
					|| sb.charAt(i)=='-'
					|| sb.charAt(i)=='^') dis=0; // ���Z�q�Ȃ�0
			else if (	digit.matches(String.valueOf(sb.charAt(i)))
					|| unit.matches(String.valueOf(sb.charAt(i)))
					|| Character.isDigit(sb.charAt(i))) dis = 1; // �������E�Z�p�����Ȃ�1
			else if (bigUnit.matches(String.valueOf(sb.charAt(i)))) dis = 2; // �������Ȃ�2
			else dis=0; // ���̑���0;
			
			switch (dis) {
			case 0: if (prev == 1) sb.insert(i, ")"); i++; break; // ���Z�q�Ȃǂ̑O�������̎�
			case 1: if (prev != 1) sb.insert(i, "("); i++; break; // �����̑O�������łȂ���
			case 2: sb.insert(i,  ")"); i++; break; // �������̎�
			}
			prev=dis;
			
		}
		formula = sb.toString();
		
		// ��`��𐔎��ɁB
		for (int i=0; i<formula.length(); i++) {
			for (int j=0; j<9; j++) { 
				if (formula.charAt(i)==digit.charAt(j)) { 
					formula = formula.replace(formula.charAt(i), Character.forDigit(j+1,10));
				}
			}
		}

		// �\�S�������
		for (int i=0; i<formula.length(); i++) {
			for (int j=0; j<3; j++) {
				if (formula.charAt(i) == unit.charAt(j)) {
					StringBuffer buf = new StringBuffer(formula.length());
					if (i!=0) if (Character.isDigit(formula.charAt(i-1))==true) buf.append("*"); // �O�̕����������̏ꍇ*�𓪂ɕt����
					buf.append("10^"+(j+1));
					if (i<formula.length()-1) if (Character.isDigit(formula.charAt(i+1))==true) buf.append("+"); // ��̕����������̏ꍇ+������
					formula = formula.replaceFirst(String.valueOf(formula.charAt(i)), buf.toString());
				}
			}
		}
		
		// ������������
		for (int i=0; i<formula.length(); i++) {
			for (int j=0; j<bigUnit.length();j++) {
				if (formula.charAt(i) == bigUnit.charAt(j)) {
					StringBuffer buf = new StringBuffer(formula.length());
					buf.append("*(10^4)^"+(j+1));
					if (i<formula.length()-1) {
						if (Character.isDigit(formula.charAt(i+1))==true) buf.append("+"); // ��̕����������̏ꍇ+������
					}
					formula = formula.replaceFirst(String.valueOf(formula.charAt(i)), buf.toString());
					
				}
			}
		}
		return formula;
	}
	// TODO: ������Ɋ܂܂��S�p�����𔼊p�����ɕϊ�����
	// TODO: ���{��i�u�����v�u�����v�Ȃǁj�ɂ�

	public static double solve(String formula) {
		// ���ʂ��o�Ă��邽�тɌv�Z���Ēu������
		{
			int i=formula.length()-1, j;
			while (i>=0) {
				if (formula.charAt(i)=='(') { // i�����ڂ�(
					for (j=i; j<formula.length(); j++) {
						if (formula.charAt(j)==')') break; // j�����ڂ�)
					}
					// "(1+2)" -> "3"�̂悤�Ɋ��ʂ��폜���Ē��g���v�Z���Ēu��
					formula = formula.replace(formula.substring(i, j+1), solveAndReplace(formula.substring(i+1, j)));
				}
				i--;
			}
		}
		
		formula = solveAndReplace(formula);
		// �����������double�^��
		double answer = Double.valueOf(formula);
		return answer;
	}

	public static String solveAndReplace(String formula) {
		ArrayList<Double> numbers = new ArrayList<Double>(); // �v�Z���鐔�������̏��ԂŊi�[������́B
		ArrayList<Integer> operators = new ArrayList<Integer>();// �Z�p�L�������̏��ԂŊi�[������́B������^,*,/,+,-�̏���0,1,2,3,4�Ƃ���B
		operators.add(3); //�ŏ��̍��ɂ�+�����Ă���ƍl����B

		//�X�y�[�X�ƃR���}�������B
		StringBuffer buf = new StringBuffer(formula.length());
		for (int i=0; i<formula.length(); i++) {
			if (formula.charAt(i)==' ' || formula.charAt(i)=='�@' || formula.charAt(i) == ',') continue; //�X�y�[�X�ƃR���}���΂��B
			buf.append(formula.charAt(i));
		}
		formula = buf.toString();
		buf = new StringBuffer(formula.length());

		// �����𕪗ނ���B
		buf.append(formula.charAt(0)); // �ŏ��̕����͐����ł���Ɖ���
		for(int i=1; i<formula.length(); i++) {
			if (Character.isDigit(formula.charAt(i))==true || formula.charAt(i)=='.') { // �����܂��͏����_�̎�
				buf.append(formula.charAt(i));
				continue;
			}
			else { // �����܂��͏����_�ȊO�̎�
				{ // ���O�̐���numbers�ɓ����
					numbers.add(Double.parseDouble(buf.toString()));
					buf = new StringBuffer(formula.length());
				}

				{ // ���Z�q�̔���
					if (formula.charAt(i)=='^') operators.add(0);
					else if (formula.charAt(i)=='*') operators.add(1);
					else if (formula.charAt(i)=='/' || formula.charAt(i) == '��') operators.add(2);
					else if (formula.charAt(i)=='+') operators.add(3);
					else if (formula.charAt(i)=='-') operators.add(4);
					else {
						System.out.println("�������v�Z�ł��܂���ł���");
						System.exit(1);
					}
				}
			}
		}
		numbers.add(Double.parseDouble(buf.toString())); // �Ō�̕����������ł���Ɖ��肵�čŌ�̐�������B

		// ���ނ������╄�����g���Čv�Z����B
		// �܂��͗ݏ���v�Z����
		for (int i=1; i<numbers.size(); i++) {
			if (operators.get(i) == 0) {
				numbers.set(i-1, Math.pow(numbers.get(i-1), numbers.get(i))); // �O�̐���ݏ悵�����ɕύX
				numbers.set(i, 1.0); // ��̐���1�ɕύX
				operators.set(i, 1); // ��̐��ɕt�����Z�q��*�ɕύX
			}
		}
		
		// ���ɏ揜���v�Z����
		for (int i=1; i<numbers.size(); i++) {
			switch (operators.get(i)) {
			case 1: {
				numbers.set(i, numbers.get(i-1)*numbers.get(i)); // ��̐����|�������ɕύX
				operators.set(i, 3); // ��̐��ɕt�����Z�q��+�ɕύX
				numbers.set(i-1, 0.0); // �O�̐���0�ɕύX
				operators.set(i-1, 3); // �O�̐��ɕt�����Z�q��+�ɕύX
				break; // +2*3 -> +0)*6 �ƂȂ� 
			}
			case 2: {
				numbers.set(i, numbers.get(i-1)/numbers.get(i)); // ��̐������������ɕύX
				operators.set(i, 3); // ��̐��ɕt�����Z�q��+�ɕύX
				numbers.set(i-1, 0.0); // �O�̐���0�ɕύX
				operators.set(i-1, 3); // �O�̐��ɕt�����Z�q��+�ɕύX
				break; // +2/3 -> +0)*0.666  �ƂȂ� 
			}
			}
		}

		// �������v�Z
		double answer = 0.0;
		for (int i=0; i<numbers.size(); i++){
			switch (operators.get(i)) {
			// case0, 1, 2�͊��Ɍv�Z�ς�
			case 3: answer+=numbers.get(i); break;
			case 4: answer-=numbers.get(i); break;
			}
		}
		
		formula = String.valueOf(answer);
		return formula;
	}

	public static void main(String[] args) {
		System.out.println(solve("1+2*3"));
	}

}
