> [!NOTE]
> Arnoは開発が終了し、後継プロジェクトである [rossoc](https://github.com/arthur87/rossoc) に移行しました。

# Arno
ArnoはSQLをArduinoのソースコードに変換するコンパイラです。

## SQL

例えば、
`select din11,ain20 from arduino where (din1 = 0 and din2 <=1 ) or din3 != 9`
のようなSQLは次のようなArduinoのソースコードに変換されます。


```
void setup() {
  Serial.begin(9600);
  pinMode(1, INPUT);
  pinMode(2, INPUT);
  pinMode(3, INPUT);
  pinMode(11, INPUT);
}

void loop() {
  int din1 = digitalRead(1);
  int din2 = digitalRead(2);
  int din3 = digitalRead(3);
  int din11 = digitalRead(11);
  int ain20 = analogRead(20);
  if( (din1 == 0 && din2 <= 1) || din3 != 9 ) {
    Serial.print("[ ");
    Serial.print(din11);
    Serial.print(" ");
    Serial.print(ain20);
    Serial.print(" ");
    Serial.print("]");
  }
  delay(1000);
}
```

ArnoはSELECT文のみサポートしています。
SELECT文の構文は以下のとおりです。

```
SELECT 列名1, 列名2, ...
FROM テーブル名
WHERE 条件
```

列名に指定できる値は、`din1`から`din20`と`ain1`から`ain20`です。
dinはdigitalPin、ainはanalogPinに対応しています。

テーブル名に指定できる値は、`arduino`です。

条件の列名に指定できる値は、`din1`から`din20`と`ain1`から`ain20`です。
また、使用できる比較演算子は以下のとおりです。

| 種類 | 意味 |
|---|---|
| a = b |	aとbが等しい |
| a < b	|	aはbより小さい |
| a > b	|	aはbより大きい |
| a <= b | aはb以下（b含む） |
| a >= b | aはb以上（b含む） |
| a != b |	aとbは等しくない |

複数の条件を組み合わせる場合は。論理演算子を使用します。
使用できる論理演算子は以下のとおりです。

| 種類 | 意味 |
|---|---|
| a AND b |	aかつbの場合 |
| a OR b	|	aまたはbの場合 |


## コンパイラオプション

コンパイラオプションは以下のとおりです。

```
$ java -jar Arno.jar -help
Usage: arnoc [-options] input
 -i <arg>  Input SQL.
 -o <file>   Write output to <file>.
 -speed <arg>  Sets the data rate in bits per second (baud) for serial data transmission.
 -delay <arg>  Pauses the program for the amount of time (in miliseconds) specified as parameter.
 -version   Shows compiler version and quit.
 -help  Prints this message and quit.
```

SQLをArduinoのソースコードに変換するには、以下のコマンドを実行します。

```
$ java -jar Arno.jar -i "select din11,ain20 from arduino where (din1 = 0 and din2 <=1 ) or din3 != 9"
```

`-speed` を指定すると`Serial.begin(speed)`を変更することができます。
省略した場合は`9600`です。

`-delay` を指定すると`delay(ms)`を変更することができます。
省略した場合は`1000`です。
