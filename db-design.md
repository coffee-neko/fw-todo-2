# DB設計書

## 1. 目的と対象

この文書は、ToDoアプリケーションで保存するデータベース（データを保管する仕組み）の設計を定める。

- 使用するテーブル（データを行と列で保管する表）は `todos` の1つだけとする。
- `todos` の1行は、1件のやることを表す。
- この文書のDDL（テーブルを作るためのSQL文）は設計用であり、ここでは実行しない。

## 2. テーブル一覧

| テーブル名 | 用途 | 行の単位 |
|---|---|---|
| `todos` | やることを保存する | 1件のやること |

## 3. todosテーブルのカラム定義

| カラム名 | 意味 | データ型 | NULLを許可するか | 初期値・自動処理 | キー |
|---|---|---|---|---|---|
| `id` | 1件を見分ける番号 | `BIGINT` | 許可しない | 自動で1ずつ増加する | 主キー |
| `title` | やること | `VARCHAR(255)` | 許可しない | なし | なし |
| `detail` | メモ | `VARCHAR(255)` | 許可する | なし | なし |
| `category` | ジャンル | `VARCHAR(255)` | 許可しない | なし | なし |
| `priority` | 優先度（1: 高、2: 中、3: 低） | `INT` | 許可しない | `2` | なし |
| `due_date` | 期限 | `DATE` | 許可する | なし | なし |
| `completed` | 完了したかどうか | `BOOLEAN` | 許可しない | `FALSE`（未完了） | なし |
| `created_at` | 登録日時 | `DATETIME` | 許可しない | 登録時にMySQLが自動設定する | なし |
| `updated_at` | 最終更新日時 | `DATETIME` | 許可しない | 登録時と更新時にMySQLが自動設定する | なし |

## 4. 要件とカラムの対応

| 要件 | 対応するカラム | DB設計での扱い |
|---|---|---|
| やることを必須で登録し、255文字以内にする | `title` | `VARCHAR(255)` と `NOT NULL` を使用する |
| メモを任意で登録し、255文字以内にする | `detail` | `VARCHAR(255)` とし、NULLを許可する |
| ジャンルを必須で選択する | `category` | `VARCHAR(255)` と `NOT NULL` を使用する。選択肢の5種類はアプリケーション側で制御する |
| 優先度を高・中・低から選び、初期値を中にする | `priority` | `INT`、`NOT NULL`、既定値 `2` を使用する |
| 期限を任意で入力し、期限順に並び替える | `due_date` | `DATE` とし、NULLを許可する |
| 編集時に完了・未完了を設定し、一覧で状態を表示する | `completed` | `BOOLEAN`、`NOT NULL`、既定値 `FALSE` を使用する |
| 編集・削除で対象の1件を指定する | `id` | 自動採番する主キーとする |
| 登録日時と最終更新日時を自動で管理する | `created_at`、`updated_at` | MySQLの自動設定を使用する |
| 登録・編集・削除の記録に対象の番号を残す | `id` | 記録に利用する番号として参照する。ログ用のテーブルは追加しない |

## 5. 入力規則とDBでの扱い

- `title`、`category`、`priority` の必須判定、空白だけの入力の判定、画面に表示するエラーメッセージは、アプリケーション側で行う。
- `category` の選択肢は「デザイン」「マーケティング」「プログラミング」「資格」「就職活動」の5つとする。元仕様に合わせ、DBには追加の制約を設けず、選択入力で制御する。
- `priority` は `1`（高）、`2`（中）、`3`（低）を使用する。元仕様に合わせ、DBには追加の制約を設けず、選択入力で制御する。
- `id`、`created_at`、`updated_at` は利用者の入力項目として表示しない。

## 6. DDL

以下はMySQLで `todos` テーブルを作成するDDLである。実行はしない。

```sql
CREATE TABLE todos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    detail VARCHAR(255) NULL,
    category VARCHAR(255) NOT NULL,
    priority INT NOT NULL DEFAULT 2,
    due_date DATE NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
```

## 7. 仕様にない内容の追加

仕様書および要件定義書にないテーブル、カラム、索引、DBの追加制約は追加していない。
