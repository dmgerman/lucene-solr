begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.standard.processors
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|processors
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

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
name|LinkedList
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|CachingTokenFilter
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|queryParser
operator|.
name|core
operator|.
name|QueryNodeException
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
name|queryParser
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|FieldQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|FuzzyQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|GroupQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|NoTokenFoundQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|ParametricQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|QueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|QuotedFieldQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|TextableQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|TokenizedPhraseQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|processors
operator|.
name|QueryNodeProcessorImpl
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
name|queryParser
operator|.
name|standard
operator|.
name|config
operator|.
name|AnalyzerAttribute
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
name|queryParser
operator|.
name|standard
operator|.
name|config
operator|.
name|PositionIncrementsAttribute
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
name|queryParser
operator|.
name|standard
operator|.
name|nodes
operator|.
name|MultiPhraseQueryNode
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
name|queryParser
operator|.
name|standard
operator|.
name|nodes
operator|.
name|StandardBooleanQueryNode
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
name|queryParser
operator|.
name|standard
operator|.
name|nodes
operator|.
name|WildcardQueryNode
import|;
end_import

begin_comment
comment|/**  * This processor verifies if the attribute {@link AnalyzerQueryNodeProcessor}  * is defined in the {@link QueryConfigHandler}. If it is and the analyzer is  * not<code>null</code>, it looks for every {@link FieldQueryNode} that is not  * {@link WildcardQueryNode}, {@link FuzzyQueryNode} or  * {@link ParametricQueryNode} contained in the query node tree, then it applies  * the analyzer to that {@link FieldQueryNode} object.<br/>  *<br/>  * If the analyzer return only one term, the returned term is set to the  * {@link FieldQueryNode} and it's returned.<br/>  *<br/>  * If the analyzer return more than one term, a {@link TokenizedPhraseQueryNode}  * or {@link MultiPhraseQueryNode} is created, whether there is one or more  * terms at the same position, and it's returned.<br/>  *<br/>  * If no term is returned by the analyzer a {@link NoTokenFoundQueryNode} object  * is returned.<br/>  *   * @see Analyzer  * @see TokenStream  */
end_comment

begin_class
DECL|class|AnalyzerQueryNodeProcessor
specifier|public
class|class
name|AnalyzerQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|positionIncrementsEnabled
specifier|private
name|boolean
name|positionIncrementsEnabled
decl_stmt|;
DECL|method|AnalyzerQueryNodeProcessor
specifier|public
name|AnalyzerQueryNodeProcessor
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|QueryNode
name|process
parameter_list|(
name|QueryNode
name|queryTree
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|getQueryConfigHandler
argument_list|()
operator|.
name|hasAttribute
argument_list|(
name|AnalyzerAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|AnalyzerAttribute
operator|.
name|class
argument_list|)
operator|.
name|getAnalyzer
argument_list|()
expr_stmt|;
name|this
operator|.
name|positionIncrementsEnabled
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|getQueryConfigHandler
argument_list|()
operator|.
name|hasAttribute
argument_list|(
name|PositionIncrementsAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
if|if
condition|(
name|getQueryConfigHandler
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementsAttribute
operator|.
name|class
argument_list|)
operator|.
name|isPositionIncrementsEnabled
argument_list|()
condition|)
block|{
name|this
operator|.
name|positionIncrementsEnabled
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|this
operator|.
name|analyzer
operator|!=
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|process
argument_list|(
name|queryTree
argument_list|)
return|;
block|}
block|}
return|return
name|queryTree
return|;
block|}
annotation|@
name|Override
DECL|method|postProcessNode
specifier|protected
name|QueryNode
name|postProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|node
operator|instanceof
name|TextableQueryNode
operator|&&
operator|!
operator|(
name|node
operator|instanceof
name|WildcardQueryNode
operator|)
operator|&&
operator|!
operator|(
name|node
operator|instanceof
name|FuzzyQueryNode
operator|)
operator|&&
operator|!
operator|(
name|node
operator|instanceof
name|ParametricQueryNode
operator|)
condition|)
block|{
name|FieldQueryNode
name|fieldNode
init|=
operator|(
operator|(
name|FieldQueryNode
operator|)
name|node
operator|)
decl_stmt|;
name|String
name|text
init|=
name|fieldNode
operator|.
name|getTextAsString
argument_list|()
decl_stmt|;
name|String
name|field
init|=
name|fieldNode
operator|.
name|getFieldAsString
argument_list|()
decl_stmt|;
name|TokenStream
name|source
decl_stmt|;
try|try
block|{
name|source
operator|=
name|this
operator|.
name|analyzer
operator|.
name|reusableTokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
name|source
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e1
argument_list|)
throw|;
block|}
name|CachingTokenFilter
name|buffer
init|=
operator|new
name|CachingTokenFilter
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
literal|null
decl_stmt|;
name|int
name|numTokens
init|=
literal|0
decl_stmt|;
name|int
name|positionCount
init|=
literal|0
decl_stmt|;
name|boolean
name|severalTokensAtSamePosition
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|buffer
operator|.
name|hasAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|posIncrAtt
operator|=
name|buffer
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
try|try
block|{
while|while
condition|(
name|buffer
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|numTokens
operator|++
expr_stmt|;
name|int
name|positionIncrement
init|=
operator|(
name|posIncrAtt
operator|!=
literal|null
operator|)
condition|?
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
else|:
literal|1
decl_stmt|;
if|if
condition|(
name|positionIncrement
operator|!=
literal|0
condition|)
block|{
name|positionCount
operator|+=
name|positionIncrement
expr_stmt|;
block|}
else|else
block|{
name|severalTokensAtSamePosition
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
try|try
block|{
comment|// rewind the buffer stream
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// close original stream - all tokens buffered
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
operator|!
name|buffer
operator|.
name|hasAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
operator|new
name|NoTokenFoundQueryNode
argument_list|()
return|;
block|}
name|CharTermAttribute
name|termAtt
init|=
name|buffer
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|numTokens
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|NoTokenFoundQueryNode
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|numTokens
operator|==
literal|1
condition|)
block|{
name|String
name|term
init|=
literal|null
decl_stmt|;
try|try
block|{
name|boolean
name|hasNext
decl_stmt|;
name|hasNext
operator|=
name|buffer
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
assert|assert
name|hasNext
operator|==
literal|true
assert|;
name|term
operator|=
name|termAtt
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore, because we know the number of tokens
block|}
name|fieldNode
operator|.
name|setText
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|fieldNode
return|;
block|}
elseif|else
if|if
condition|(
name|severalTokensAtSamePosition
operator|||
operator|!
operator|(
name|node
operator|instanceof
name|QuotedFieldQueryNode
operator|)
condition|)
block|{
if|if
condition|(
name|positionCount
operator|==
literal|1
operator|||
operator|!
operator|(
name|node
operator|instanceof
name|QuotedFieldQueryNode
operator|)
condition|)
block|{
comment|// no phrase query:
name|LinkedList
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
operator|new
name|LinkedList
argument_list|<
name|QueryNode
argument_list|>
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
name|numTokens
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
literal|null
decl_stmt|;
try|try
block|{
name|boolean
name|hasNext
init|=
name|buffer
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
assert|assert
name|hasNext
operator|==
literal|true
assert|;
name|term
operator|=
name|termAtt
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore, because we know the number of tokens
block|}
name|children
operator|.
name|add
argument_list|(
operator|new
name|FieldQueryNode
argument_list|(
name|field
argument_list|,
name|term
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|positionCount
operator|==
literal|1
condition|)
return|return
operator|new
name|GroupQueryNode
argument_list|(
operator|new
name|StandardBooleanQueryNode
argument_list|(
name|children
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
else|else
return|return
operator|new
name|StandardBooleanQueryNode
argument_list|(
name|children
argument_list|,
literal|false
argument_list|)
return|;
block|}
else|else
block|{
comment|// phrase query:
name|MultiPhraseQueryNode
name|mpq
init|=
operator|new
name|MultiPhraseQueryNode
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FieldQueryNode
argument_list|>
name|multiTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|FieldQueryNode
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|position
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|int
name|termGroupCount
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|numTokens
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
literal|null
decl_stmt|;
name|int
name|positionIncrement
init|=
literal|1
decl_stmt|;
try|try
block|{
name|boolean
name|hasNext
init|=
name|buffer
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
assert|assert
name|hasNext
operator|==
literal|true
assert|;
name|term
operator|=
name|termAtt
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
name|posIncrAtt
operator|!=
literal|null
condition|)
block|{
name|positionIncrement
operator|=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore, because we know the number of tokens
block|}
if|if
condition|(
name|positionIncrement
operator|>
literal|0
operator|&&
name|multiTerms
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|FieldQueryNode
name|termNode
range|:
name|multiTerms
control|)
block|{
if|if
condition|(
name|this
operator|.
name|positionIncrementsEnabled
condition|)
block|{
name|termNode
operator|.
name|setPositionIncrement
argument_list|(
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termNode
operator|.
name|setPositionIncrement
argument_list|(
name|termGroupCount
argument_list|)
expr_stmt|;
block|}
name|mpq
operator|.
name|add
argument_list|(
name|termNode
argument_list|)
expr_stmt|;
block|}
comment|// Only increment once for each "group" of
comment|// terms that were in the same position:
name|termGroupCount
operator|++
expr_stmt|;
name|multiTerms
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|position
operator|+=
name|positionIncrement
expr_stmt|;
name|multiTerms
operator|.
name|add
argument_list|(
operator|new
name|FieldQueryNode
argument_list|(
name|field
argument_list|,
name|term
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|FieldQueryNode
name|termNode
range|:
name|multiTerms
control|)
block|{
if|if
condition|(
name|this
operator|.
name|positionIncrementsEnabled
condition|)
block|{
name|termNode
operator|.
name|setPositionIncrement
argument_list|(
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termNode
operator|.
name|setPositionIncrement
argument_list|(
name|termGroupCount
argument_list|)
expr_stmt|;
block|}
name|mpq
operator|.
name|add
argument_list|(
name|termNode
argument_list|)
expr_stmt|;
block|}
return|return
name|mpq
return|;
block|}
block|}
else|else
block|{
name|TokenizedPhraseQueryNode
name|pq
init|=
operator|new
name|TokenizedPhraseQueryNode
argument_list|()
decl_stmt|;
name|int
name|position
init|=
operator|-
literal|1
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
name|numTokens
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
literal|null
decl_stmt|;
name|int
name|positionIncrement
init|=
literal|1
decl_stmt|;
try|try
block|{
name|boolean
name|hasNext
init|=
name|buffer
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
assert|assert
name|hasNext
operator|==
literal|true
assert|;
name|term
operator|=
name|termAtt
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
name|posIncrAtt
operator|!=
literal|null
condition|)
block|{
name|positionIncrement
operator|=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore, because we know the number of tokens
block|}
name|FieldQueryNode
name|newFieldNode
init|=
operator|new
name|FieldQueryNode
argument_list|(
name|field
argument_list|,
name|term
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|positionIncrementsEnabled
condition|)
block|{
name|position
operator|+=
name|positionIncrement
expr_stmt|;
name|newFieldNode
operator|.
name|setPositionIncrement
argument_list|(
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newFieldNode
operator|.
name|setPositionIncrement
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|pq
operator|.
name|add
argument_list|(
name|newFieldNode
argument_list|)
expr_stmt|;
block|}
return|return
name|pq
return|;
block|}
block|}
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|preProcessNode
specifier|protected
name|QueryNode
name|preProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|setChildrenOrder
specifier|protected
name|List
argument_list|<
name|QueryNode
argument_list|>
name|setChildrenOrder
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|children
return|;
block|}
block|}
end_class

end_unit

