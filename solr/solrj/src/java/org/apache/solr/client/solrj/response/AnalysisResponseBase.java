begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|List
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

begin_comment
comment|/**  * A base class for all analysis responses.  *  *  * @since solr 1.4  */
end_comment

begin_class
DECL|class|AnalysisResponseBase
specifier|public
class|class
name|AnalysisResponseBase
extends|extends
name|SolrResponseBase
block|{
comment|/**    * Parses the given named list and builds a list of analysis phases form it. Expects a named list of the form:    *<p/>    *<pre><code>    *&lt;lst name="index"&gt;    *&lt;arr name="Tokenizer"&gt;    *&lt;str name="text"&gt;the_text&lt;/str&gt;    *&lt;str name="rawText"&gt;the_raw_text&lt;/str&gt; (optional)    *&lt;str name="type"&gt;the_type&lt;/str&gt;    *&lt;int name="start"&gt;1&lt;/str&gt;    *&lt;int name="end"&gt;3&lt;/str&gt;    *&lt;int name="position"&gt;1&lt;/str&gt;    *&lt;bool name="match"&gt;true | false&lt;/bool&gt; (optional)    *&lt;/arr&gt;    *&lt;arr name="Filter1"&gt;    *&lt;str name="text"&gt;the_text&lt;/str&gt;    *&lt;str name="rawText"&gt;the_raw_text&lt;/str&gt; (optional)    *&lt;str name="type"&gt;the_type&lt;/str&gt;    *&lt;int name="start"&gt;1&lt;/str&gt;    *&lt;int name="end"&gt;3&lt;/str&gt;    *&lt;int name="position"&gt;1&lt;/str&gt;    *&lt;bool name="match"&gt;true | false&lt;/bool&gt; (optional)    *&lt;/arr&gt;    *      ...    *&lt;/lst&gt;    *</code></pre>    *    * @param phaseNL The names list to parse.    *    * @return The built analysis phases list.    */
DECL|method|buildPhases
specifier|protected
name|List
argument_list|<
name|AnalysisPhase
argument_list|>
name|buildPhases
parameter_list|(
name|NamedList
argument_list|<
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|phaseNL
parameter_list|)
block|{
name|List
argument_list|<
name|AnalysisPhase
argument_list|>
name|phases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|phaseNL
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|phaseEntry
range|:
name|phaseNL
control|)
block|{
name|AnalysisPhase
name|phase
init|=
operator|new
name|AnalysisPhase
argument_list|(
name|phaseEntry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|tokens
init|=
name|phaseEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|token
range|:
name|tokens
control|)
block|{
name|TokenInfo
name|tokenInfo
init|=
name|buildTokenInfo
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|phase
operator|.
name|addTokenInfo
argument_list|(
name|tokenInfo
argument_list|)
expr_stmt|;
block|}
name|phases
operator|.
name|add
argument_list|(
name|phase
argument_list|)
expr_stmt|;
block|}
return|return
name|phases
return|;
block|}
comment|/**    * Parses the given named list and builds a token infoform it. Expects a named list of the form:    *<p/>    *<pre><code>    *&lt;arr name="Tokenizer"&gt;    *&lt;str name="text"&gt;the_text&lt;/str&gt;    *&lt;str name="rawText"&gt;the_raw_text&lt;/str&gt; (optional)    *&lt;str name="type"&gt;the_type&lt;/str&gt;    *&lt;int name="start"&gt;1&lt;/str&gt;    *&lt;int name="end"&gt;3&lt;/str&gt;    *&lt;int name="position"&gt;1&lt;/str&gt;    *&lt;bool name="match"&gt;true | false&lt;/bool&gt; (optional)    *&lt;/arr&gt;    *</code></pre>    *    * @param tokenNL The named list to parse.    *    * @return The built token info.    */
DECL|method|buildTokenInfo
specifier|protected
name|TokenInfo
name|buildTokenInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|tokenNL
parameter_list|)
block|{
name|String
name|text
init|=
operator|(
name|String
operator|)
name|tokenNL
operator|.
name|get
argument_list|(
literal|"text"
argument_list|)
decl_stmt|;
name|String
name|rawText
init|=
operator|(
name|String
operator|)
name|tokenNL
operator|.
name|get
argument_list|(
literal|"rawText"
argument_list|)
decl_stmt|;
name|String
name|type
init|=
operator|(
name|String
operator|)
name|tokenNL
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
name|int
name|start
init|=
operator|(
name|Integer
operator|)
name|tokenNL
operator|.
name|get
argument_list|(
literal|"start"
argument_list|)
decl_stmt|;
name|int
name|end
init|=
operator|(
name|Integer
operator|)
name|tokenNL
operator|.
name|get
argument_list|(
literal|"end"
argument_list|)
decl_stmt|;
name|int
name|position
init|=
operator|(
name|Integer
operator|)
name|tokenNL
operator|.
name|get
argument_list|(
literal|"position"
argument_list|)
decl_stmt|;
name|Boolean
name|match
init|=
operator|(
name|Boolean
operator|)
name|tokenNL
operator|.
name|get
argument_list|(
literal|"match"
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenInfo
argument_list|(
name|text
argument_list|,
name|rawText
argument_list|,
name|type
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|position
argument_list|,
operator|(
name|match
operator|==
literal|null
condition|?
literal|false
else|:
name|match
operator|)
argument_list|)
return|;
block|}
comment|//================================================= Inner Classes ==================================================
comment|/**    * A phase in the analysis process. The phase holds the tokens produced in this phase and the name of the class that    * produced them.    */
DECL|class|AnalysisPhase
specifier|public
specifier|static
class|class
name|AnalysisPhase
block|{
DECL|field|className
specifier|private
specifier|final
name|String
name|className
decl_stmt|;
DECL|field|tokens
specifier|private
name|List
argument_list|<
name|TokenInfo
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|AnalysisPhase
name|AnalysisPhase
parameter_list|(
name|String
name|className
parameter_list|)
block|{
name|this
operator|.
name|className
operator|=
name|className
expr_stmt|;
block|}
comment|/**      * The name of the class (analyzer, tokenzier, or filter) that produced the token stream for this phase.      *      * @return The name of the class that produced the token stream for this phase.      */
DECL|method|getClassName
specifier|public
name|String
name|getClassName
parameter_list|()
block|{
return|return
name|className
return|;
block|}
DECL|method|addTokenInfo
specifier|private
name|void
name|addTokenInfo
parameter_list|(
name|TokenInfo
name|tokenInfo
parameter_list|)
block|{
name|tokens
operator|.
name|add
argument_list|(
name|tokenInfo
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns a list of tokens which represent the token stream produced in this phase.      *      * @return A list of tokens which represent the token stream produced in this phase.      */
DECL|method|getTokens
specifier|public
name|List
argument_list|<
name|TokenInfo
argument_list|>
name|getTokens
parameter_list|()
block|{
return|return
name|tokens
return|;
block|}
block|}
comment|/**    * Holds all information of a token as part of an analysis phase.    */
DECL|class|TokenInfo
specifier|public
specifier|static
class|class
name|TokenInfo
block|{
DECL|field|text
specifier|private
specifier|final
name|String
name|text
decl_stmt|;
DECL|field|rawText
specifier|private
specifier|final
name|String
name|rawText
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|start
specifier|private
specifier|final
name|int
name|start
decl_stmt|;
DECL|field|end
specifier|private
specifier|final
name|int
name|end
decl_stmt|;
DECL|field|position
specifier|private
specifier|final
name|int
name|position
decl_stmt|;
DECL|field|match
specifier|private
specifier|final
name|boolean
name|match
decl_stmt|;
comment|/**      * Constructs a new TokenInfo.      *      * @param text     The text of the token      * @param rawText  The raw text of the token. If the token is stored in the index in a special format (e.g.      *                 dates or padded numbers) this argument should hold this value. If the token is stored as is,      *                 then this value should be {@code null}.      * @param type     The type fo the token (typically either {@code word} or {@code<ALPHANUM>} though it depends      *                 on the tokenizer/filter used).      * @param start    The start position of the token in the original text where it was extracted from.      * @param end      The end position of the token in the original text where it was extracted from.      * @param position The position of the token within the token stream.      * @param match    Indicates whether this token matches one of the the query tokens.      */
DECL|method|TokenInfo
name|TokenInfo
parameter_list|(
name|String
name|text
parameter_list|,
name|String
name|rawText
parameter_list|,
name|String
name|type
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|int
name|position
parameter_list|,
name|boolean
name|match
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|rawText
operator|=
name|rawText
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
name|this
operator|.
name|match
operator|=
name|match
expr_stmt|;
block|}
comment|/**      * Returns the text of the token.      *      * @return The text of the token.      */
DECL|method|getText
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|text
return|;
block|}
comment|/**      * Returns the raw text of the token. If the token is index in a special format (e.g. date or paddded numbers)      * it will be returned as the raw text. Returns {@code null} if the token is indexed as is.      *      * @return Returns the raw text of the token.      */
DECL|method|getRawText
specifier|public
name|String
name|getRawText
parameter_list|()
block|{
return|return
name|rawText
return|;
block|}
comment|/**      * Returns the type of the token. Typically this will be {@code word} or {@code<ALPHANUM>}, but it really      * depends on the tokenizer and filters that are used.      *      * @return The type of the token.      */
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**      * Returns the start position of this token within the text it was originally extracted from.      *      * @return The start position of this token within the text it was originally extracted from.      */
DECL|method|getStart
specifier|public
name|int
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
comment|/**      * Returns the end position of this token within the text it was originally extracted from.      *      * @return The end position of this token within the text it was originally extracted from.      */
DECL|method|getEnd
specifier|public
name|int
name|getEnd
parameter_list|()
block|{
return|return
name|end
return|;
block|}
comment|/**      * Returns the position of this token within the produced token stream.      *      * @return The position of this token within the produced token stream.      */
DECL|method|getPosition
specifier|public
name|int
name|getPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
comment|/**      * Returns whether this token matches one of the query tokens (if query analysis is performed).      *      * @return Whether this token matches one of the query tokens (if query analysis is performed).      */
DECL|method|isMatch
specifier|public
name|boolean
name|isMatch
parameter_list|()
block|{
return|return
name|match
return|;
block|}
block|}
block|}
end_class

end_unit

