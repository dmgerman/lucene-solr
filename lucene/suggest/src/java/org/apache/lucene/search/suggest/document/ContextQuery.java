begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|document
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|IndexSearcher
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
name|Weight
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
name|BytesRef
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
name|BytesRefBuilder
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
name|IntsRef
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
name|IntsRefBuilder
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
name|automaton
operator|.
name|Automata
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
name|automaton
operator|.
name|Automaton
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
name|automaton
operator|.
name|Operations
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
name|automaton
operator|.
name|RegExp
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
name|fst
operator|.
name|Util
import|;
end_import

begin_comment
comment|/**  * A {@link CompletionQuery} that match documents specified by  * a wrapped {@link CompletionQuery} supporting boosting and/or filtering  * by specified contexts.  *<p>  * Use this query against {@link ContextSuggestField}  *<p>  * Example of using a {@link CompletionQuery} with boosted  * contexts:  *<pre class="prettyprint">  *  CompletionQuery completionQuery = ...;  *  ContextQuery query = new ContextQuery(completionQuery);  *  query.addContext("context1", 2);  *  query.addContext("context2", 1);  *</pre>  *<p>  * NOTE:  *<ul>  *<li>  *    This query can be constructed with  *    {@link PrefixCompletionQuery}, {@link RegexCompletionQuery}  *    or {@link FuzzyCompletionQuery} query.  *</li>  *<li>  *     To suggest across all contexts with the same boost,  *     use '*' as the context in {@link #addContext(CharSequence)})}.  *     This can be combined with specific contexts with different boosts.  *</li>  *<li>  *     To apply the same boost to multiple contexts sharing the same prefix,  *     Use {@link #addContext(CharSequence, float, boolean)} with the common  *     context prefix, boost and set<code>exact</code> to false.  *<li>  *     Using this query against a {@link SuggestField} (not context enabled),  *     would yield results ignoring any context filtering/boosting  *</li>  *</ul>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ContextQuery
specifier|public
class|class
name|ContextQuery
extends|extends
name|CompletionQuery
block|{
DECL|field|contexts
specifier|private
name|Map
argument_list|<
name|CharSequence
argument_list|,
name|ContextMetaData
argument_list|>
name|contexts
decl_stmt|;
comment|/** Inner completion query */
DECL|field|query
specifier|protected
name|CompletionQuery
name|query
decl_stmt|;
comment|/**    * Constructs a context completion query that matches    * documents specified by<code>query</code>.    *<p>    * Use {@link #addContext(CharSequence, float, boolean)}    * to add context(s) with boost    */
DECL|method|ContextQuery
specifier|public
name|ContextQuery
parameter_list|(
name|CompletionQuery
name|query
parameter_list|)
block|{
name|super
argument_list|(
name|query
operator|.
name|getTerm
argument_list|()
argument_list|,
name|query
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|instanceof
name|ContextQuery
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"'query' parameter must not be of type "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
throw|;
block|}
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|contexts
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds an exact context with default boost of 1    */
DECL|method|addContext
specifier|public
name|void
name|addContext
parameter_list|(
name|CharSequence
name|context
parameter_list|)
block|{
name|addContext
argument_list|(
name|context
argument_list|,
literal|1f
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds an exact context with boost    */
DECL|method|addContext
specifier|public
name|void
name|addContext
parameter_list|(
name|CharSequence
name|context
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|addContext
argument_list|(
name|context
argument_list|,
name|boost
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds a context with boost, set<code>exact</code> to false    * if the context is a prefix of any indexed contexts    */
DECL|method|addContext
specifier|public
name|void
name|addContext
parameter_list|(
name|CharSequence
name|context
parameter_list|,
name|float
name|boost
parameter_list|,
name|boolean
name|exact
parameter_list|)
block|{
if|if
condition|(
name|boost
operator|<
literal|0f
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"'boost' must be>= 0"
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|context
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|ContextSuggestField
operator|.
name|CONTEXT_SEPARATOR
operator|==
name|context
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal value ["
operator|+
name|context
operator|+
literal|"] UTF-16 codepoint [0x"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
operator|(
name|int
operator|)
name|context
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
operator|+
literal|"] at position "
operator|+
name|i
operator|+
literal|" is a reserved character"
argument_list|)
throw|;
block|}
block|}
name|contexts
operator|.
name|put
argument_list|(
name|context
argument_list|,
operator|new
name|ContextMetaData
argument_list|(
name|boost
argument_list|,
name|exact
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|CharSequence
name|context
range|:
name|contexts
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"contexts"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":["
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|ContextMetaData
name|metaData
init|=
name|contexts
operator|.
name|get
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|metaData
operator|.
name|exact
operator|==
literal|false
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|metaData
operator|.
name|boost
operator|!=
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Float
operator|.
name|toString
argument_list|(
name|metaData
operator|.
name|boost
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
operator|+
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
name|IntsRefBuilder
name|scratch
init|=
operator|new
name|IntsRefBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|IntsRef
argument_list|,
name|Float
argument_list|>
name|contextMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|contexts
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|TreeSet
argument_list|<
name|Integer
argument_list|>
name|contextLengths
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|CompletionWeight
name|innerWeight
init|=
operator|(
operator|(
name|CompletionWeight
operator|)
name|query
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|)
operator|)
decl_stmt|;
name|Automaton
name|contextsAutomaton
init|=
literal|null
decl_stmt|;
name|Automaton
name|gap
init|=
name|Automata
operator|.
name|makeChar
argument_list|(
name|ContextSuggestField
operator|.
name|CONTEXT_SEPARATOR
argument_list|)
decl_stmt|;
comment|// if separators are preserved the fst contains a SEP_LABEL
comment|// behind each gap. To have a matching automaton, we need to
comment|// include the SEP_LABEL in the query as well
name|gap
operator|=
name|Operations
operator|.
name|concatenate
argument_list|(
name|gap
argument_list|,
name|Operations
operator|.
name|optional
argument_list|(
name|Automata
operator|.
name|makeChar
argument_list|(
name|CompletionAnalyzer
operator|.
name|SEP_LABEL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Automaton
name|prefixAutomaton
init|=
name|Operations
operator|.
name|concatenate
argument_list|(
name|gap
argument_list|,
name|innerWeight
operator|.
name|getAutomaton
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Automaton
name|matchAllAutomaton
init|=
operator|new
name|RegExp
argument_list|(
literal|".*"
argument_list|)
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|CharSequence
argument_list|,
name|ContextMetaData
argument_list|>
name|entry
range|:
name|contexts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Automaton
name|contextAutomaton
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|contextAutomaton
operator|=
name|Operations
operator|.
name|concatenate
argument_list|(
name|matchAllAutomaton
argument_list|,
name|prefixAutomaton
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|ContextMetaData
name|contextMetaData
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|contextMap
operator|.
name|put
argument_list|(
name|IntsRef
operator|.
name|deepCopyOf
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
name|ref
argument_list|,
name|scratch
argument_list|)
argument_list|)
argument_list|,
name|contextMetaData
operator|.
name|boost
argument_list|)
expr_stmt|;
name|contextLengths
operator|.
name|add
argument_list|(
name|scratch
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|contextAutomaton
operator|=
name|Automata
operator|.
name|makeString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextMetaData
operator|.
name|exact
condition|)
block|{
name|contextAutomaton
operator|=
name|Operations
operator|.
name|concatenate
argument_list|(
name|contextAutomaton
argument_list|,
name|prefixAutomaton
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|contextAutomaton
operator|=
name|Operations
operator|.
name|concatenate
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|contextAutomaton
argument_list|,
name|matchAllAutomaton
argument_list|,
name|prefixAutomaton
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|contextsAutomaton
operator|==
literal|null
condition|)
block|{
name|contextsAutomaton
operator|=
name|contextAutomaton
expr_stmt|;
block|}
else|else
block|{
name|contextsAutomaton
operator|=
name|Operations
operator|.
name|union
argument_list|(
name|contextsAutomaton
argument_list|,
name|contextAutomaton
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|contexts
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|addContext
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
name|contextsAutomaton
operator|=
name|Operations
operator|.
name|concatenate
argument_list|(
name|matchAllAutomaton
argument_list|,
name|prefixAutomaton
argument_list|)
expr_stmt|;
block|}
name|contextsAutomaton
operator|=
name|Operations
operator|.
name|determinize
argument_list|(
name|contextsAutomaton
argument_list|,
name|Operations
operator|.
name|DEFAULT_MAX_DETERMINIZED_STATES
argument_list|)
expr_stmt|;
name|int
index|[]
name|contextLengthArray
init|=
operator|new
name|int
index|[
name|contextLengths
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iterator
init|=
name|contextLengths
operator|.
name|descendingIterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|contextLengthArray
index|[
name|i
index|]
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ContextCompletionWeight
argument_list|(
name|this
argument_list|,
name|contextsAutomaton
argument_list|,
name|innerWeight
argument_list|,
name|contextMap
argument_list|,
name|contextLengthArray
argument_list|)
return|;
block|}
DECL|class|ContextMetaData
specifier|private
specifier|static
class|class
name|ContextMetaData
block|{
DECL|field|boost
specifier|private
specifier|final
name|float
name|boost
decl_stmt|;
DECL|field|exact
specifier|private
specifier|final
name|boolean
name|exact
decl_stmt|;
DECL|method|ContextMetaData
specifier|private
name|ContextMetaData
parameter_list|(
name|float
name|boost
parameter_list|,
name|boolean
name|exact
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
name|this
operator|.
name|exact
operator|=
name|exact
expr_stmt|;
block|}
block|}
DECL|class|ContextCompletionWeight
specifier|private
class|class
name|ContextCompletionWeight
extends|extends
name|CompletionWeight
block|{
DECL|field|contextMap
specifier|private
specifier|final
name|Map
argument_list|<
name|IntsRef
argument_list|,
name|Float
argument_list|>
name|contextMap
decl_stmt|;
DECL|field|contextLengths
specifier|private
specifier|final
name|int
index|[]
name|contextLengths
decl_stmt|;
DECL|field|innerWeight
specifier|private
specifier|final
name|CompletionWeight
name|innerWeight
decl_stmt|;
DECL|field|scratch
specifier|private
specifier|final
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|currentBoost
specifier|private
name|float
name|currentBoost
decl_stmt|;
DECL|field|currentContext
specifier|private
name|CharSequence
name|currentContext
decl_stmt|;
DECL|method|ContextCompletionWeight
specifier|public
name|ContextCompletionWeight
parameter_list|(
name|CompletionQuery
name|query
parameter_list|,
name|Automaton
name|automaton
parameter_list|,
name|CompletionWeight
name|innerWeight
parameter_list|,
name|Map
argument_list|<
name|IntsRef
argument_list|,
name|Float
argument_list|>
name|contextMap
parameter_list|,
name|int
index|[]
name|contextLengths
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|,
name|automaton
argument_list|)
expr_stmt|;
name|this
operator|.
name|contextMap
operator|=
name|contextMap
expr_stmt|;
name|this
operator|.
name|contextLengths
operator|=
name|contextLengths
expr_stmt|;
name|this
operator|.
name|innerWeight
operator|=
name|innerWeight
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextMatch
specifier|protected
name|void
name|setNextMatch
parameter_list|(
name|IntsRef
name|pathPrefix
parameter_list|)
block|{
name|IntsRef
name|ref
init|=
name|pathPrefix
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// check if the pathPrefix matches any
comment|// defined context, longer context first
for|for
control|(
name|int
name|contextLength
range|:
name|contextLengths
control|)
block|{
if|if
condition|(
name|contextLength
operator|>
name|pathPrefix
operator|.
name|length
condition|)
block|{
continue|continue;
block|}
name|ref
operator|.
name|length
operator|=
name|contextLength
expr_stmt|;
if|if
condition|(
name|contextMap
operator|.
name|containsKey
argument_list|(
name|ref
argument_list|)
condition|)
block|{
name|currentBoost
operator|=
name|contextMap
operator|.
name|get
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|ref
operator|.
name|length
operator|=
name|pathPrefix
operator|.
name|length
expr_stmt|;
name|ref
operator|.
name|offset
operator|=
name|contextLength
expr_stmt|;
while|while
condition|(
name|ref
operator|.
name|ints
index|[
name|ref
operator|.
name|offset
index|]
operator|!=
name|ContextSuggestField
operator|.
name|CONTEXT_SEPARATOR
condition|)
block|{
name|ref
operator|.
name|offset
operator|++
expr_stmt|;
assert|assert
name|ref
operator|.
name|offset
operator|<
name|ref
operator|.
name|length
assert|;
block|}
assert|assert
name|ref
operator|.
name|ints
index|[
name|ref
operator|.
name|offset
index|]
operator|==
name|ContextSuggestField
operator|.
name|CONTEXT_SEPARATOR
operator|:
literal|"expected CONTEXT_SEPARATOR at offset="
operator|+
name|ref
operator|.
name|offset
assert|;
if|if
condition|(
name|ref
operator|.
name|offset
operator|>
name|pathPrefix
operator|.
name|offset
condition|)
block|{
name|currentContext
operator|=
name|Util
operator|.
name|toBytesRef
argument_list|(
operator|new
name|IntsRef
argument_list|(
name|pathPrefix
operator|.
name|ints
argument_list|,
name|pathPrefix
operator|.
name|offset
argument_list|,
name|ref
operator|.
name|offset
argument_list|)
argument_list|,
name|scratch
argument_list|)
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|currentContext
operator|=
literal|null
expr_stmt|;
block|}
name|ref
operator|.
name|offset
operator|++
expr_stmt|;
if|if
condition|(
name|ref
operator|.
name|ints
index|[
name|ref
operator|.
name|offset
index|]
operator|==
name|CompletionAnalyzer
operator|.
name|SEP_LABEL
condition|)
block|{
name|ref
operator|.
name|offset
operator|++
expr_stmt|;
block|}
name|innerWeight
operator|.
name|setNextMatch
argument_list|(
name|ref
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|// unknown context
name|ref
operator|.
name|length
operator|=
name|pathPrefix
operator|.
name|length
expr_stmt|;
name|currentBoost
operator|=
name|contexts
operator|.
name|get
argument_list|(
literal|"*"
argument_list|)
operator|.
name|boost
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|pathPrefix
operator|.
name|offset
init|;
name|i
operator|<
name|pathPrefix
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|pathPrefix
operator|.
name|ints
index|[
name|i
index|]
operator|==
name|ContextSuggestField
operator|.
name|CONTEXT_SEPARATOR
condition|)
block|{
if|if
condition|(
name|i
operator|>
name|pathPrefix
operator|.
name|offset
condition|)
block|{
name|currentContext
operator|=
name|Util
operator|.
name|toBytesRef
argument_list|(
operator|new
name|IntsRef
argument_list|(
name|pathPrefix
operator|.
name|ints
argument_list|,
name|pathPrefix
operator|.
name|offset
argument_list|,
name|i
argument_list|)
argument_list|,
name|scratch
argument_list|)
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|currentContext
operator|=
literal|null
expr_stmt|;
block|}
name|ref
operator|.
name|offset
operator|=
operator|++
name|i
expr_stmt|;
assert|assert
name|ref
operator|.
name|offset
operator|<
name|ref
operator|.
name|length
operator|:
literal|"input should not end with the context separator"
assert|;
if|if
condition|(
name|pathPrefix
operator|.
name|ints
index|[
name|i
index|]
operator|==
name|CompletionAnalyzer
operator|.
name|SEP_LABEL
condition|)
block|{
name|ref
operator|.
name|offset
operator|++
expr_stmt|;
assert|assert
name|ref
operator|.
name|offset
operator|<
name|ref
operator|.
name|length
operator|:
literal|"input should not end with a context separator followed by SEP_LABEL"
assert|;
block|}
name|ref
operator|.
name|length
operator|-=
name|ref
operator|.
name|offset
expr_stmt|;
name|innerWeight
operator|.
name|setNextMatch
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|context
specifier|protected
name|CharSequence
name|context
parameter_list|()
block|{
return|return
name|currentContext
return|;
block|}
annotation|@
name|Override
DECL|method|boost
specifier|protected
name|float
name|boost
parameter_list|()
block|{
return|return
name|currentBoost
operator|+
name|innerWeight
operator|.
name|boost
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

