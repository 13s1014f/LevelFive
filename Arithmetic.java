import java.util.ArrayList;


public class Arithmetic {

	// 文字列に含まれる漢数字を数式にするメソッド
	// FIXME: 括弧をつける処理がうまくいかないことが多い。現状大きな修正を要する可能性がある。
	public static String kanjiToNumeral(String formula) {
		String digit = "一二三四五六七八九";
		String unit = "十百千";
		String bigUnit = "万億兆";

		// 後々の計算のために「万」「億」「兆」単位に括弧をつける：十二万五百五 -> (十二)万(五百五)
		StringBuilder sb = new StringBuilder(); // formulaをコピーして色々いじるためのStringBuilder
		sb.append(formula);
		int prev=0, dis=0; // 前の文字と今の文字の属性。演算子など数字以外:0, 千以下の漢数字または算用数字:1, 万以上の漢数字:2
		
		for (int i=0; i<sb.length(); i++) {
			if (	sb.charAt(i)=='*'
					|| sb.charAt(i)=='/' || sb.charAt(i)=='÷'
					|| sb.charAt(i)=='+'
					|| sb.charAt(i)=='-'
					|| sb.charAt(i)=='^') dis=0; // 演算子なら0
			else if (	digit.matches(String.valueOf(sb.charAt(i)))
					|| unit.matches(String.valueOf(sb.charAt(i)))
					|| Character.isDigit(sb.charAt(i))) dis = 1; // 漢数字・算用数字なら1
			else if (bigUnit.matches(String.valueOf(sb.charAt(i)))) dis = 2; // 万億兆なら2
			else dis=0; // その他は0;
			
			switch (dis) {
			case 0: if (prev == 1) sb.insert(i, ")"); i++; break; // 演算子などの前が数字の時
			case 1: if (prev != 1) sb.insert(i, "("); i++; break; // 数字の前が数字でない時
			case 2: sb.insert(i,  ")"); i++; break; // 万億兆の時
			}
			prev=dis;
			
		}
		formula = sb.toString();
		
		// 一～九を数字に。
		for (int i=0; i<formula.length(); i++) {
			for (int j=0; j<9; j++) { 
				if (formula.charAt(i)==digit.charAt(j)) { 
					formula = formula.replace(formula.charAt(i), Character.forDigit(j+1,10));
				}
			}
		}

		// 十百千を処理
		for (int i=0; i<formula.length(); i++) {
			for (int j=0; j<3; j++) {
				if (formula.charAt(i) == unit.charAt(j)) {
					StringBuffer buf = new StringBuffer(formula.length());
					if (i!=0) if (Character.isDigit(formula.charAt(i-1))==true) buf.append("*"); // 前の文字が数字の場合*を頭に付ける
					buf.append("10^"+(j+1));
					if (i<formula.length()-1) if (Character.isDigit(formula.charAt(i+1))==true) buf.append("+"); // 後の文字が数字の場合+をつける
					formula = formula.replaceFirst(String.valueOf(formula.charAt(i)), buf.toString());
				}
			}
		}
		
		// 万億兆を処理
		for (int i=0; i<formula.length(); i++) {
			for (int j=0; j<bigUnit.length();j++) {
				if (formula.charAt(i) == bigUnit.charAt(j)) {
					StringBuffer buf = new StringBuffer(formula.length());
					buf.append("*(10^4)^"+(j+1));
					if (i<formula.length()-1) {
						if (Character.isDigit(formula.charAt(i+1))==true) buf.append("+"); // 後の文字が数字の場合+をつける
					}
					formula = formula.replaceFirst(String.valueOf(formula.charAt(i)), buf.toString());
					
				}
			}
		}
		return formula;
	}
	// TODO: 文字列に含まれる全角文字を半角文字に変換する
	// TODO: 日本語（「足す」「引く」など）にも

	public static double solve(String formula) {
		// 括弧が出てくるたびに計算して置換する
		{
			int i=formula.length()-1, j;
			while (i>=0) {
				if (formula.charAt(i)=='(') { // i文字目が(
					for (j=i; j<formula.length(); j++) {
						if (formula.charAt(j)==')') break; // j文字目が)
					}
					// "(1+2)" -> "3"のように括弧を削除して中身を計算して置換
					formula = formula.replace(formula.substring(i, j+1), solveAndReplace(formula.substring(i+1, j)));
				}
				i--;
			}
		}
		
		formula = solveAndReplace(formula);
		// 得た文字列をdouble型に
		double answer = Double.valueOf(formula);
		return answer;
	}

	public static String solveAndReplace(String formula) {
		ArrayList<Double> numbers = new ArrayList<Double>(); // 計算する数字をその順番で格納するもの。
		ArrayList<Integer> operators = new ArrayList<Integer>();// 算術記号をその順番で格納するもの。ただし^,*,/,+,-の順で0,1,2,3,4とする。
		operators.add(3); //最初の項には+がついていると考える。

		//スペースとコンマを消す。
		StringBuffer buf = new StringBuffer(formula.length());
		for (int i=0; i<formula.length(); i++) {
			if (formula.charAt(i)==' ' || formula.charAt(i)=='　' || formula.charAt(i) == ',') continue; //スペースとコンマを飛ばす。
			buf.append(formula.charAt(i));
		}
		formula = buf.toString();
		buf = new StringBuffer(formula.length());

		// 文字を分類する。
		buf.append(formula.charAt(0)); // 最初の文字は数字であると仮定
		for(int i=1; i<formula.length(); i++) {
			if (Character.isDigit(formula.charAt(i))==true || formula.charAt(i)=='.') { // 数字または小数点の時
				buf.append(formula.charAt(i));
				continue;
			}
			else { // 数字または小数点以外の時
				{ // 直前の数をnumbersに入れる
					numbers.add(Double.parseDouble(buf.toString()));
					buf = new StringBuffer(formula.length());
				}

				{ // 演算子の判定
					if (formula.charAt(i)=='^') operators.add(0);
					else if (formula.charAt(i)=='*') operators.add(1);
					else if (formula.charAt(i)=='/' || formula.charAt(i) == '÷') operators.add(2);
					else if (formula.charAt(i)=='+') operators.add(3);
					else if (formula.charAt(i)=='-') operators.add(4);
					else {
						System.out.println("正しく計算できませんでした");
						System.exit(1);
					}
				}
			}
		}
		numbers.add(Double.parseDouble(buf.toString())); // 最後の文字も数字であると仮定して最後の数を入れる。

		// 分類した数や符号を使って計算する。
		// まずは累乗を計算する
		for (int i=1; i<numbers.size(); i++) {
			if (operators.get(i) == 0) {
				numbers.set(i-1, Math.pow(numbers.get(i-1), numbers.get(i))); // 前の数を累乗した数に変更
				numbers.set(i, 1.0); // 後の数を1に変更
				operators.set(i, 1); // 後の数に付く演算子を*に変更
			}
		}
		
		// 次に乗除を計算する
		for (int i=1; i<numbers.size(); i++) {
			switch (operators.get(i)) {
			case 1: {
				numbers.set(i, numbers.get(i-1)*numbers.get(i)); // 後の数を掛けた数に変更
				operators.set(i, 3); // 後の数に付く演算子を+に変更
				numbers.set(i-1, 0.0); // 前の数を0に変更
				operators.set(i-1, 3); // 前の数に付く演算子を+に変更
				break; // +2*3 -> +0)*6 となる 
			}
			case 2: {
				numbers.set(i, numbers.get(i-1)/numbers.get(i)); // 後の数を割った数に変更
				operators.set(i, 3); // 後の数に付く演算子を+に変更
				numbers.set(i-1, 0.0); // 前の数を0に変更
				operators.set(i-1, 3); // 前の数に付く演算子を+に変更
				break; // +2/3 -> +0)*0.666  となる 
			}
			}
		}

		// 加減を計算
		double answer = 0.0;
		for (int i=0; i<numbers.size(); i++){
			switch (operators.get(i)) {
			// case0, 1, 2は既に計算済み
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
