begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.expressions
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|js
operator|.
name|JavascriptCompiler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|SortField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestExpressionSortField
specifier|public
class|class
name|TestExpressionSortField
extends|extends
name|LuceneTestCase
block|{
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
throws|throws
name|Exception
block|{
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"sqrt(_score) + ln(popularity)"
argument_list|)
decl_stmt|;
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"_score"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"popularity"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|)
argument_list|)
expr_stmt|;
name|SortField
name|sf
init|=
name|expr
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"<expr(sqrt(_score) + ln(popularity))>!"
argument_list|,
name|sf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"sqrt(_score) + ln(popularity)"
argument_list|)
decl_stmt|;
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"_score"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"popularity"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleBindings
name|otherBindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
name|otherBindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"_score"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|otherBindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"popularity"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|)
argument_list|)
expr_stmt|;
name|SortField
name|sf1
init|=
name|expr
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// different order
name|SortField
name|sf2
init|=
name|expr
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|sf1
operator|.
name|equals
argument_list|(
name|sf2
argument_list|)
argument_list|)
expr_stmt|;
comment|// different bindings
name|sf2
operator|=
name|expr
operator|.
name|getSortField
argument_list|(
name|otherBindings
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sf1
operator|.
name|equals
argument_list|(
name|sf2
argument_list|)
argument_list|)
expr_stmt|;
comment|// different expression
name|Expression
name|other
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"popularity/2"
argument_list|)
decl_stmt|;
name|sf2
operator|=
name|other
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sf1
operator|.
name|equals
argument_list|(
name|sf2
argument_list|)
argument_list|)
expr_stmt|;
comment|// null
name|assertFalse
argument_list|(
name|sf1
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// same instance:
name|assertEquals
argument_list|(
name|sf1
argument_list|,
name|sf1
argument_list|)
expr_stmt|;
block|}
DECL|method|testNeedsScores
specifier|public
name|void
name|testNeedsScores
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
comment|// refers to score directly
name|Expression
name|exprA
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"_score"
argument_list|)
decl_stmt|;
comment|// constant
name|Expression
name|exprB
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"0"
argument_list|)
decl_stmt|;
comment|// field
name|Expression
name|exprC
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"intfield"
argument_list|)
decl_stmt|;
comment|// score + constant
name|Expression
name|exprD
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"_score + 0"
argument_list|)
decl_stmt|;
comment|// field + constant
name|Expression
name|exprE
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"intfield + 0"
argument_list|)
decl_stmt|;
comment|// expression + constant (score ref'd)
name|Expression
name|exprF
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"a + 0"
argument_list|)
decl_stmt|;
comment|// expression + constant
name|Expression
name|exprG
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"e + 0"
argument_list|)
decl_stmt|;
comment|// several variables (score ref'd)
name|Expression
name|exprH
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"b / c + e * g - sqrt(f)"
argument_list|)
decl_stmt|;
comment|// several variables
name|Expression
name|exprI
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"b / c + e * g"
argument_list|)
decl_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"_score"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"intfield"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|)
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
literal|"a"
argument_list|,
name|exprA
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
literal|"b"
argument_list|,
name|exprB
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
literal|"c"
argument_list|,
name|exprC
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
literal|"d"
argument_list|,
name|exprD
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
literal|"e"
argument_list|,
name|exprE
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
literal|"f"
argument_list|,
name|exprF
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
literal|"g"
argument_list|,
name|exprG
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
literal|"h"
argument_list|,
name|exprH
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
literal|"i"
argument_list|,
name|exprI
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exprA
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
operator|.
name|needsScores
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|exprB
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
operator|.
name|needsScores
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|exprC
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
operator|.
name|needsScores
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exprD
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
operator|.
name|needsScores
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|exprE
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
operator|.
name|needsScores
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exprF
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
operator|.
name|needsScores
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|exprG
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
operator|.
name|needsScores
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exprH
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|true
argument_list|)
operator|.
name|needsScores
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|exprI
operator|.
name|getSortField
argument_list|(
name|bindings
argument_list|,
literal|false
argument_list|)
operator|.
name|needsScores
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

