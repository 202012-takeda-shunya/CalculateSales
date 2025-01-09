package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)


		//処理内容2-1
		//拡張子がrcd、かつファイル名が数字8桁のファイルを検索し、該当したファイルを売上ファイルとして保持する。

		// ファイルの一覧を配列に格納
		String dirPath = args[0];
		File[] files = new File(dirPath).listFiles();

		//rcdファイルを抽出してArraylistに追加
		List<File> rcdFiles = new ArrayList<>();

		for(int i = 0; i < files.length ; i++) {
			String fileName = files[i].getName();
			//条件:数字8桁 末尾に「.rcd」がくるもの
			if(fileName.matches("^[0-9]{8}.rcd$")) {
				rcdFiles.add(files[i]);
			}
		}


		//処理内容2-2
		//売上ファイルを読み込み、支店コード、売上額を抽出。抽出した売上額を該当する支店の合計金額にそれぞれ加算する。

		//rcdFilesにある売上ファイルの数だけ繰り返す。
		for(int i = 0; i < rcdFiles.size(); i++) {

			//支店定義ファイル読み込み(readFileメソッド)を参考に売上ファイルの中身を読み込む。
			BufferedReader br = null;

			try {
				File file = rcdFiles.get(i);
				FileReader fr = new FileReader(file);
				br = new BufferedReader(fr);

				String line;
				int count = 0;
				String code = null;
				String sale = null;
				// 一行ずつ読み込む
				while((line = br.readLine()) != null) {
					count +=1;
					if(count == 1) {
						code = line;
					}else if(count == 2);
						sale = line;
				}

				System.out.println("支店コード:"+ code + "売上金額:" + sale);

				//売上金額をlong型に変換
				long fileSale = Long.parseLong(sale);

				//売上金額を加算して、Mapの値を変更
				Long saleAmount = branchSales.get(code) + fileSale;
				branchSales.replace(code,saleAmount);


			} catch(IOException e) {
				System.out.println(UNKNOWN_ERROR);

			} finally {
				// ファイルを開いている場合
				if(br != null) {
					try {
						// ファイルを閉じる
						br.close();
					} catch(IOException e) {
						System.out.println(UNKNOWN_ERROR);
					}
				}
			}

		}


		// 支店別集計ファイル書き込み処理
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}

	}

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {
			File file = new File(path, fileName);
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。
				//処理内容1-2
				//支店定義ファイルの中身を一行ずつ読み込み、全ての支店コードとそれに対応する支店名を保持する


				String [] linevalues = line.split(",");

				//支店コードと支店名を追加
				branchNames.put(linevalues[0], linevalues[1]);
				//支店コードを追加し、売上金額は「0」を登録
				long value = 0;
				branchSales.put(linevalues[0], (Long)value);
				System.out.println(line);
			}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}



	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		//処理内容3-1
		//支店別集計ファイルを作成する。
		//支店別集計ファイルのフォーマットに従い、全支店の支店コード、支店名、合計金額を出力


		BufferedWriter bw =null;
		try {
			String filePath = path + "\\" + fileName;
			File file = new File(filePath);
			System.out.println(file.exists());
			file.createNewFile();

			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			for (String key : branchNames.keySet()) {
				//keyという変数には、Mapから取得したキーが代入されています。
				//拡張for⽂で繰り返されているので、1つ⽬のキーが取得できたら、
				//2つ⽬の取得...といったように、次々とkeyという変数に上書きされていきます。

				String branchName = branchNames.get(key) ;
				Long branchSale = branchSales.get(key);

				bw.write(key + "," + branchName + "," + branchSale);

				bw.newLine();
			}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(bw != null) {
				try {
					// ファイルを閉じる
					bw.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

}
