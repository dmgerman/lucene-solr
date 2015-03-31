begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.luke
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|luke
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *  * @since solr 1.3  */
end_comment

begin_enum
DECL|enum|FieldFlag
specifier|public
enum|enum
name|FieldFlag
block|{
DECL|enum constant|INDEXED
name|INDEXED
argument_list|(
literal|'I'
argument_list|,
literal|"Indexed"
argument_list|)
block|,
DECL|enum constant|TOKENIZED
name|TOKENIZED
argument_list|(
literal|'T'
argument_list|,
literal|"Tokenized"
argument_list|)
block|,
DECL|enum constant|STORED
name|STORED
argument_list|(
literal|'S'
argument_list|,
literal|"Stored"
argument_list|)
block|,
DECL|enum constant|DOC_VALUES
name|DOC_VALUES
argument_list|(
literal|'D'
argument_list|,
literal|"DocValues"
argument_list|)
block|,
DECL|enum constant|MULTI_VALUED
name|MULTI_VALUED
argument_list|(
literal|'M'
argument_list|,
literal|"Multivalued"
argument_list|)
block|,
DECL|enum constant|TERM_VECTOR_STORED
name|TERM_VECTOR_STORED
argument_list|(
literal|'V'
argument_list|,
literal|"TermVector Stored"
argument_list|)
block|,
DECL|enum constant|TERM_VECTOR_OFFSET
name|TERM_VECTOR_OFFSET
argument_list|(
literal|'o'
argument_list|,
literal|"Store Offset With TermVector"
argument_list|)
block|,
DECL|enum constant|TERM_VECTOR_POSITION
name|TERM_VECTOR_POSITION
argument_list|(
literal|'p'
argument_list|,
literal|"Store Position With TermVector"
argument_list|)
block|,
DECL|enum constant|TERM_VECTOR_PAYLOADS
name|TERM_VECTOR_PAYLOADS
argument_list|(
literal|'y'
argument_list|,
literal|"Store Payloads With TermVector"
argument_list|)
block|,
DECL|enum constant|OMIT_NORMS
name|OMIT_NORMS
argument_list|(
literal|'O'
argument_list|,
literal|"Omit Norms"
argument_list|)
block|,
DECL|enum constant|OMIT_TF
name|OMIT_TF
argument_list|(
literal|'F'
argument_list|,
literal|"Omit Term Frequencies& Positions"
argument_list|)
block|,
DECL|enum constant|OMIT_POSITIONS
name|OMIT_POSITIONS
argument_list|(
literal|'P'
argument_list|,
literal|"Omit Positions"
argument_list|)
block|,
DECL|enum constant|STORE_OFFSETS_WITH_POSITIONS
name|STORE_OFFSETS_WITH_POSITIONS
argument_list|(
literal|'H'
argument_list|,
literal|"Store Offsets with Positions"
argument_list|)
block|,
DECL|enum constant|LAZY
name|LAZY
argument_list|(
literal|'L'
argument_list|,
literal|"Lazy"
argument_list|)
block|,
DECL|enum constant|BINARY
name|BINARY
argument_list|(
literal|'B'
argument_list|,
literal|"Binary"
argument_list|)
block|,
DECL|enum constant|SORT_MISSING_FIRST
name|SORT_MISSING_FIRST
argument_list|(
literal|'f'
argument_list|,
literal|"Sort Missing First"
argument_list|)
block|,
DECL|enum constant|SORT_MISSING_LAST
name|SORT_MISSING_LAST
argument_list|(
literal|'l'
argument_list|,
literal|"Sort Missing Last"
argument_list|)
block|;
DECL|field|abbreviation
specifier|private
specifier|final
name|char
name|abbreviation
decl_stmt|;
DECL|field|display
specifier|private
specifier|final
name|String
name|display
decl_stmt|;
DECL|method|FieldFlag
name|FieldFlag
parameter_list|(
name|char
name|abbreviation
parameter_list|,
name|String
name|display
parameter_list|)
block|{
name|this
operator|.
name|abbreviation
operator|=
name|abbreviation
expr_stmt|;
name|this
operator|.
name|display
operator|=
name|display
expr_stmt|;
name|this
operator|.
name|display
operator|.
name|intern
argument_list|()
expr_stmt|;
comment|//QUESTION:  Need we bother here?
block|}
DECL|method|getFlag
specifier|public
specifier|static
name|FieldFlag
name|getFlag
parameter_list|(
name|char
name|abbrev
parameter_list|)
block|{
name|FieldFlag
name|result
init|=
literal|null
decl_stmt|;
name|FieldFlag
index|[]
name|vals
init|=
name|FieldFlag
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|vals
index|[
name|i
index|]
operator|.
name|getAbbreviation
argument_list|()
operator|==
name|abbrev
condition|)
block|{
name|result
operator|=
name|vals
index|[
name|i
index|]
expr_stmt|;
break|break;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|getAbbreviation
specifier|public
name|char
name|getAbbreviation
parameter_list|()
block|{
return|return
name|abbreviation
return|;
block|}
DECL|method|getDisplay
specifier|public
name|String
name|getDisplay
parameter_list|()
block|{
return|return
name|display
return|;
block|}
block|}
end_enum

end_unit

