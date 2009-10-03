begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|query
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Query
import|;
end_import

begin_class
DECL|class|FieldsQuery
specifier|public
class|class
name|FieldsQuery
extends|extends
name|SrndQuery
block|{
comment|/* mostly untested */
DECL|field|q
specifier|private
name|SrndQuery
name|q
decl_stmt|;
DECL|field|fieldNames
specifier|private
name|List
name|fieldNames
decl_stmt|;
DECL|field|fieldOp
specifier|private
specifier|final
name|char
name|fieldOp
decl_stmt|;
DECL|field|OrOperatorName
specifier|private
specifier|final
name|String
name|OrOperatorName
init|=
literal|"OR"
decl_stmt|;
comment|/* for expanded queries, not normally visible */
DECL|method|FieldsQuery
specifier|public
name|FieldsQuery
parameter_list|(
name|SrndQuery
name|q
parameter_list|,
name|List
name|fieldNames
parameter_list|,
name|char
name|fieldOp
parameter_list|)
block|{
name|this
operator|.
name|q
operator|=
name|q
expr_stmt|;
name|this
operator|.
name|fieldNames
operator|=
name|fieldNames
expr_stmt|;
name|this
operator|.
name|fieldOp
operator|=
name|fieldOp
expr_stmt|;
block|}
DECL|method|FieldsQuery
specifier|public
name|FieldsQuery
parameter_list|(
name|SrndQuery
name|q
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|char
name|fieldOp
parameter_list|)
block|{
name|this
operator|.
name|q
operator|=
name|q
expr_stmt|;
name|fieldNames
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|fieldNames
operator|.
name|add
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldOp
operator|=
name|fieldOp
expr_stmt|;
block|}
DECL|method|isFieldsSubQueryAcceptable
specifier|public
name|boolean
name|isFieldsSubQueryAcceptable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|makeLuceneQueryNoBoost
specifier|public
name|Query
name|makeLuceneQueryNoBoost
parameter_list|(
name|BasicQueryFactory
name|qf
parameter_list|)
block|{
if|if
condition|(
name|fieldNames
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|/* single field name: no new queries needed */
return|return
name|q
operator|.
name|makeLuceneQueryFieldNoBoost
argument_list|(
operator|(
name|String
operator|)
name|fieldNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|qf
argument_list|)
return|;
block|}
else|else
block|{
comment|/* OR query over the fields */
name|List
name|queries
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|Iterator
name|fni
init|=
name|getFieldNames
argument_list|()
operator|.
name|listIterator
argument_list|()
decl_stmt|;
name|SrndQuery
name|qc
decl_stmt|;
while|while
condition|(
name|fni
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|qc
operator|=
operator|(
name|SrndQuery
operator|)
name|q
operator|.
name|clone
argument_list|()
expr_stmt|;
name|queries
operator|.
name|add
argument_list|(
operator|new
name|FieldsQuery
argument_list|(
name|qc
argument_list|,
operator|(
name|String
operator|)
name|fni
operator|.
name|next
argument_list|()
argument_list|,
name|fieldOp
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|boolean
name|infix
init|=
literal|true
decl_stmt|;
name|OrQuery
name|oq
init|=
operator|new
name|OrQuery
argument_list|(
name|queries
argument_list|,
literal|true
comment|/* infix OR for field names */
argument_list|,
name|OrOperatorName
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getClass
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|", fields expanded: "
operator|+
name|oq
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|/* needs testing */
return|return
name|oq
operator|.
name|makeLuceneQueryField
argument_list|(
literal|null
argument_list|,
name|qf
argument_list|)
return|;
block|}
block|}
DECL|method|makeLuceneQueryFieldNoBoost
specifier|public
name|Query
name|makeLuceneQueryFieldNoBoost
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|BasicQueryFactory
name|qf
parameter_list|)
block|{
return|return
name|makeLuceneQueryNoBoost
argument_list|(
name|qf
argument_list|)
return|;
comment|/* use this.fieldNames instead of fieldName */
block|}
DECL|method|getFieldNames
specifier|public
name|List
name|getFieldNames
parameter_list|()
block|{
return|return
name|fieldNames
return|;
block|}
DECL|method|getFieldOperator
specifier|public
name|char
name|getFieldOperator
parameter_list|()
block|{
return|return
name|fieldOp
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|fieldNamesToString
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|fieldNamesToString
specifier|protected
name|void
name|fieldNamesToString
parameter_list|(
name|StringBuilder
name|r
parameter_list|)
block|{
name|Iterator
name|fni
init|=
name|getFieldNames
argument_list|()
operator|.
name|listIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|fni
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|fni
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|getFieldOperator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

