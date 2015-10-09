package com.github.arthur87.Arno;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.Statement;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by asakawa on 15/10/06.
 */
public class CSQL implements ExpressionVisitor, ItemsListVisitor {
    private String sql;
    private String table;
    private String where;
    private ArrayList<String> fields = new ArrayList<String>();
    private ArrayList<Expression> conditions = new ArrayList<Expression>();

    public CSQL() {
    }

    public void parser(String sql) {
        ArrayList<String> fields = new ArrayList<String>();
        Statement statement = null;

        try {
            statement = (new CCJSqlParserManager()).parse(new StringReader(sql.trim()));
        }catch (JSQLParserException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(!(statement instanceof Select))
            throw new IllegalArgumentException();

        Select select = (Select)statement;
        if(!(select.getSelectBody() instanceof PlainSelect))
            throw new UnsupportedOperationException();

        PlainSelect plainSelect = (PlainSelect)select.getSelectBody();
        if(!(plainSelect.getFromItem() instanceof Table))
            throw new UnsupportedOperationException();

        for(Object object: plainSelect.getSelectItems()) {
            SelectItem selectItem = (SelectItem)object;

            if(selectItem instanceof  SelectExpressionItem) {
                SelectExpressionItem selectExpressionItem = (SelectExpressionItem)selectItem;
                if(!(selectExpressionItem.getExpression() instanceof Column))
                    throw new UnsupportedOperationException();

                fields.add(selectExpressionItem.getExpression().toString());
            }else {
                throw new UnsupportedOperationException();
            }
        }

        if(plainSelect.getWhere() != null) {
            plainSelect.getWhere().accept(this);
            this.where = plainSelect.getWhere().toString();
        }else {
            this.where = "1";
        }

        this.sql = sql.trim();
        this.fields = fields;
        this.table = plainSelect.getFromItem().toString();
    }

    public void printDebug() {
        System.out.println("--");
        System.out.println("sql:\t" + this.sql);
        System.out.println("table:\t" + this.table);
        System.out.println("fields:\t" + this.fields.toString());
        System.out.println("where:\t" + this.where);
        System.out.println("conditions:\t[");
        for(Expression expression: this.conditions) {
            System.out.println(expression.toString());
        }
        System.out.println("]");
        System.out.println("");
    }

    public String getSql() {
        return this.sql;
    }

    public String getTable() {
        return this.table;
    }

    public String getWhere() {
        return this.where;
    }

    public ArrayList<String> getFields() {
        return this.fields;
    }

    public ArrayList<Expression> getConditions() {
        return this.conditions;
    }

    public void visit(NullValue nullValue) {
        throw new UnsupportedOperationException();
    }

    public void visit(Function function) {
        throw new UnsupportedOperationException();
    }

    public void visit(SignedExpression signedExpression) {
        throw new UnsupportedOperationException();
    }

    public void visit(JdbcParameter jdbcParameter) {
        throw new UnsupportedOperationException();
    }

    public void visit(JdbcNamedParameter jdbcNamedParameter) {
        throw new UnsupportedOperationException();
    }

    public void visit(DoubleValue doubleValue) {
        throw new UnsupportedOperationException();
    }

    public void visit(LongValue longValue) {
        throw new UnsupportedOperationException();
    }

    public void visit(HexValue hexValue) {
        throw new UnsupportedOperationException();
    }

    public void visit(DateValue dateValue) {
        throw new UnsupportedOperationException();
    }

    public void visit(TimeValue timeValue) {
        throw new UnsupportedOperationException();
    }

    public void visit(TimestampValue timestampValue) {
        throw new UnsupportedOperationException();
    }

    public void visit(Parenthesis parenthesis) {
        parenthesis.getExpression().accept(this);
    }

    public void visit(StringValue stringValue) {
        throw new UnsupportedOperationException();
    }

    public void visit(Addition addition) {
        throw new UnsupportedOperationException();
    }

    public void visit(Division division) {
        throw new UnsupportedOperationException();
    }

    public void visit(Multiplication multiplication) {
        throw new UnsupportedOperationException();
    }

    public void visit(Subtraction subtraction) {
        throw new UnsupportedOperationException();
    }

    public void visit(AndExpression andExpression) {
        andExpression.getLeftExpression().accept(this);
        andExpression.getRightExpression().accept(this);
    }

    public void visit(OrExpression orExpression) {
        orExpression.getLeftExpression().accept(this);
        orExpression.getRightExpression().accept(this);
    }

    public void visit(Between between) {
        throw new UnsupportedOperationException();
    }

    public void visit(EqualsTo equalsTo) {
        this.conditions.add(equalsTo);
    }

    public void visit(GreaterThan greaterThan) {
        this.conditions.add(greaterThan);
    }

    public void visit(GreaterThanEquals greaterThanEquals) {
        this.conditions.add(greaterThanEquals);
    }

    public void visit(InExpression inExpression) {
        throw new UnsupportedOperationException();
    }

    public void visit(IsNullExpression isNullExpression) {
        throw new UnsupportedOperationException();
    }

    public void visit(LikeExpression likeExpression) {
        throw new UnsupportedOperationException();
    }

    public void visit(MinorThan minorThan) {
        this.conditions.add(minorThan);
    }

    public void visit(MinorThanEquals minorThanEquals) {
        this.conditions.add(minorThanEquals);
    }

    public void visit(NotEqualsTo notEqualsTo) {
        this.conditions.add(notEqualsTo);
    }

    public void visit(Column tableColumn) {
        throw new UnsupportedOperationException();
    }

    public void visit(SubSelect subSelect) {
        throw new UnsupportedOperationException();
    }

    public void visit(ExpressionList expressionList) {
        throw new UnsupportedOperationException();
    }

    public void visit(MultiExpressionList multiExprList) {
        throw new UnsupportedOperationException();
    }

    public void visit(CaseExpression caseExpression) {
        throw new UnsupportedOperationException();
    }

    public void visit(WhenClause whenClause) {
        throw new UnsupportedOperationException();
    }

    public void visit(ExistsExpression existsExpression) {
        throw new UnsupportedOperationException();
    }

    public void visit(AllComparisonExpression allComparisonExpression) {
        throw new UnsupportedOperationException();
    }

    public void visit(AnyComparisonExpression anyComparisonExpression) {
        throw new UnsupportedOperationException();
    }

    public void visit(Concat concat) {
        throw new UnsupportedOperationException();
    }

    public void visit(Matches matches) {
        throw new UnsupportedOperationException();
    }

    public void visit(BitwiseAnd bitwiseAnd) {
        throw new UnsupportedOperationException();
    }

    public void visit(BitwiseOr bitwiseOr) {
        throw new UnsupportedOperationException();
    }

    public void visit(BitwiseXor bitwiseXor) {
        throw new UnsupportedOperationException();
    }

    public void visit(CastExpression cast) {
        throw new UnsupportedOperationException();
    }

    public void visit(Modulo modulo) {
        throw new UnsupportedOperationException();
    }

    public void visit(AnalyticExpression aexpr) {
        throw new UnsupportedOperationException();
    }

    public void visit(WithinGroupExpression wgexpr) {
        throw new UnsupportedOperationException();
    }

    public void visit(ExtractExpression eexpr) {
        throw new UnsupportedOperationException();
    }

    public void visit(IntervalExpression iexpr) {
        throw new UnsupportedOperationException();
    }

    public void visit(OracleHierarchicalExpression oexpr) {
        throw new UnsupportedOperationException();
    }

    public void visit(RegExpMatchOperator rexpr) {
        throw new UnsupportedOperationException();
    }

    public void visit(JsonExpression jsonExpr) {
        throw new UnsupportedOperationException();
    }

    public void visit(RegExpMySQLOperator regExpMySQLOperator) {
        throw new UnsupportedOperationException();
    }

    public void visit(UserVariable var) {
        throw new UnsupportedOperationException();
    }

    public void visit(NumericBind bind) {
        throw new UnsupportedOperationException();
    }

    public void visit(KeepExpression aexpr) {
        throw new UnsupportedOperationException();
    }

    public void visit(MySQLGroupConcat groupConcat) {
        throw new UnsupportedOperationException();
    }

    public void visit(RowConstructor rowConstructor) {
        throw new UnsupportedOperationException();
    }
}