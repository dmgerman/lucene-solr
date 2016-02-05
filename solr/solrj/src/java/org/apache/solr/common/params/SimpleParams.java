begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_comment
comment|/**  * Parameters used by the SimpleQParser.  */
end_comment

begin_interface
DECL|interface|SimpleParams
specifier|public
interface|interface
name|SimpleParams
block|{
comment|/** Query fields and boosts. */
DECL|field|QF
specifier|public
specifier|static
name|String
name|QF
init|=
literal|"qf"
decl_stmt|;
comment|/** Override the currently enabled/disabled query operators. */
DECL|field|QO
specifier|public
specifier|static
name|String
name|QO
init|=
literal|"q.operators"
decl_stmt|;
comment|/** Enables {@code AND} operator (+) */
DECL|field|AND_OPERATOR
specifier|public
specifier|static
specifier|final
name|String
name|AND_OPERATOR
init|=
literal|"AND"
decl_stmt|;
comment|/** Enables {@code NOT} operator (-) */
DECL|field|NOT_OPERATOR
specifier|public
specifier|static
specifier|final
name|String
name|NOT_OPERATOR
init|=
literal|"NOT"
decl_stmt|;
comment|/** Enables {@code OR} operator (|) */
DECL|field|OR_OPERATOR
specifier|public
specifier|static
specifier|final
name|String
name|OR_OPERATOR
init|=
literal|"OR"
decl_stmt|;
comment|/** Enables {@code PREFIX} operator (*) */
DECL|field|PREFIX_OPERATOR
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX_OPERATOR
init|=
literal|"PREFIX"
decl_stmt|;
comment|/** Enables {@code PHRASE} operator (") */
DECL|field|PHRASE_OPERATOR
specifier|public
specifier|static
specifier|final
name|String
name|PHRASE_OPERATOR
init|=
literal|"PHRASE"
decl_stmt|;
comment|/** Enables {@code PRECEDENCE} operators: {@code (} and {@code )} */
DECL|field|PRECEDENCE_OPERATORS
specifier|public
specifier|static
specifier|final
name|String
name|PRECEDENCE_OPERATORS
init|=
literal|"PRECEDENCE"
decl_stmt|;
comment|/** Enables {@code ESCAPE} operator (\) */
DECL|field|ESCAPE_OPERATOR
specifier|public
specifier|static
specifier|final
name|String
name|ESCAPE_OPERATOR
init|=
literal|"ESCAPE"
decl_stmt|;
comment|/** Enables {@code WHITESPACE} operators: ' ' '\n' '\r' '\t' */
DECL|field|WHITESPACE_OPERATOR
specifier|public
specifier|static
specifier|final
name|String
name|WHITESPACE_OPERATOR
init|=
literal|"WHITESPACE"
decl_stmt|;
comment|/** Enables {@code FUZZY} operator (~) */
DECL|field|FUZZY_OPERATOR
specifier|public
specifier|static
specifier|final
name|String
name|FUZZY_OPERATOR
init|=
literal|"FUZZY"
decl_stmt|;
comment|/** Enables {@code NEAR} operator (~) */
DECL|field|NEAR_OPERATOR
specifier|public
specifier|static
specifier|final
name|String
name|NEAR_OPERATOR
init|=
literal|"NEAR"
decl_stmt|;
block|}
end_interface

end_unit

