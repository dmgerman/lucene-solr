begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|TermEnum
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
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Lucene Dictionary: terms taken from the given field  * of a Lucene index.  *  * When using IndexReader.terms(Term) the code must not call next() on TermEnum  * as the first call to TermEnum, see: http://issues.apache.org/jira/browse/LUCENE-6  *  *  *  */
end_comment

begin_class
DECL|class|LuceneDictionary
specifier|public
class|class
name|LuceneDictionary
implements|implements
name|Dictionary
block|{
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|method|LuceneDictionary
specifier|public
name|LuceneDictionary
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
DECL|method|getWordsIterator
specifier|public
specifier|final
name|Iterator
name|getWordsIterator
parameter_list|()
block|{
return|return
operator|new
name|LuceneIterator
argument_list|()
return|;
block|}
DECL|class|LuceneIterator
specifier|final
class|class
name|LuceneIterator
implements|implements
name|Iterator
block|{
DECL|field|termEnum
specifier|private
name|TermEnum
name|termEnum
decl_stmt|;
DECL|field|actualTerm
specifier|private
name|Term
name|actualTerm
decl_stmt|;
DECL|field|hasNextCalled
specifier|private
name|boolean
name|hasNextCalled
decl_stmt|;
DECL|method|LuceneIterator
name|LuceneIterator
parameter_list|()
block|{
try|try
block|{
name|termEnum
operator|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|next
specifier|public
name|Object
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNextCalled
condition|)
block|{
name|hasNext
argument_list|()
expr_stmt|;
block|}
name|hasNextCalled
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|termEnum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
operator|(
name|actualTerm
operator|!=
literal|null
operator|)
condition|?
name|actualTerm
operator|.
name|text
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|hasNextCalled
condition|)
block|{
return|return
name|actualTerm
operator|!=
literal|null
return|;
block|}
name|hasNextCalled
operator|=
literal|true
expr_stmt|;
name|actualTerm
operator|=
name|termEnum
operator|.
name|term
argument_list|()
expr_stmt|;
comment|// if there are no words return false
if|if
condition|(
name|actualTerm
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|currentField
init|=
name|actualTerm
operator|.
name|field
argument_list|()
decl_stmt|;
comment|// if the next word doesn't have the same field return false
if|if
condition|(
name|currentField
operator|!=
name|field
condition|)
block|{
name|actualTerm
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

