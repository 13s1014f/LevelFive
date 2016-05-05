import java.awt.List;
import java.util.ArrayList;


public class Arithmetic {

	public static int Solve(String input) {
		//�S�p�ɂ��Ή��������B
		//�����ɂ��Ή��������B
		//���{��ɂ��Ή��������B

		ArrayList<Integer> numbers = new ArrayList<Integer>(); // �v�Z���鐔�������̏��ԂŊi�[������́B
		ArrayList<Integer> operators = new ArrayList<Integer>();// �Z�p�L�������̏��ԂŊi�[������́B������*,/,+,-�̏���0,1,2,3�Ƃ���B
		operators.add(2); //�ŏ��̍��ɂ�+�����Ă���ƍl����B
		
		
		//�X�y�[�X�������B
		StringBuffer buf = new StringBuffer(input.length());
		for (int i=0; i<input.length(); i++) {
			if (input.charAt(i)==' ' || input.charAt(i)=='�@') continue; //�X�y�[�X���΂��B
			buf.append(input.charAt(i));
		}
		input = buf.toString();
		buf.delete(0, buf.length());

		// �����𕪗ނ���B
		buf.append(input.charAt(0));
		for(int i=1; i<input.length(); i++) {
			if (Character.isDigit(input.charAt(i))==true) { //�����̎�
				buf.append(input.charAt(i));
				continue;
			}
			else { //�����ȊO�̎�
				try {
					Integer.parseInt(buf.toString());
					numbers.add(Integer.parseInt(buf.toString()));
					buf.delete(0,buf.length());
				}
				catch(NumberFormatException e) {
						System.out.println("��O");
				} finally {
					if (input.charAt(i)=='*') operators.add(0);
					else if (input.charAt(i)=='/') operators.add(1);
					else if (input.charAt(i)=='+') operators.add(2);
					else if (input.charAt(i)=='-') operators.add(3);
					else {
						System.out.println("��O����");
						System.exit(1);
					}
				}
			}
		}
		numbers.add(Integer.parseInt(buf.toString())); //�Ō�̐�������B
		
		// ���ނ������╄�����g���Čv�Z����B
		int answer = 0;
		for (int i=0; i<numbers.size(); i++){
			if(operators.get(i)==0) answer*=numbers.get(i);
			else if(operators.get(i)==1) answer/=numbers.get(i);
			else if(operators.get(i)==2) answer+=numbers.get(i);
			else if(operators.get(i)==3) answer-=numbers.get(i);
		}
		

		return answer; //
	}

	public static void main(String[] args) {
		System.out.println(Solve("1+2+3+4+5"));

	}

}
