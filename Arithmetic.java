import java.awt.List;
import java.util.ArrayList;


public class Arithmetic {

	public static int Solve(String input) {
		//全角にも対応したい。
		//漢字にも対応したい。
		//日本語にも対応したい。

		ArrayList<Integer> numbers = new ArrayList<Integer>(); // 計算する数字をその順番で格納するもの。
		ArrayList<Integer> operators = new ArrayList<Integer>();// 算術記号をその順番で格納するもの。ただし*,/,+,-の順で0,1,2,3とする。
		operators.add(2); //最初の項には+がついていると考える。
		
		
		//スペースを消す。
		StringBuffer buf = new StringBuffer(input.length());
		for (int i=0; i<input.length(); i++) {
			if (input.charAt(i)==' ' || input.charAt(i)=='　') continue; //スペースを飛ばす。
			buf.append(input.charAt(i));
		}
		input = buf.toString();
		buf.delete(0, buf.length());

		// 文字を分類する。
		buf.append(input.charAt(0));
		for(int i=1; i<input.length(); i++) {
			if (Character.isDigit(input.charAt(i))==true) { //数字の時
				buf.append(input.charAt(i));
				continue;
			}
			else { //数字以外の時
				try {
					Integer.parseInt(buf.toString());
					numbers.add(Integer.parseInt(buf.toString()));
					buf.delete(0,buf.length());
				}
				catch(NumberFormatException e) {
						System.out.println("例外");
				} finally {
					if (input.charAt(i)=='*') operators.add(0);
					else if (input.charAt(i)=='/') operators.add(1);
					else if (input.charAt(i)=='+') operators.add(2);
					else if (input.charAt(i)=='-') operators.add(3);
					else {
						System.out.println("例外処理");
						System.exit(1);
					}
				}
			}
		}
		numbers.add(Integer.parseInt(buf.toString())); //最後の数を入れる。
		
		// 分類した数や符号を使って計算する。
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
