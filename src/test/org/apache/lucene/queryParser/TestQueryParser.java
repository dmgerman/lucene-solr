begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|*
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
name|*
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
name|*
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
name|*
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
name|*
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
name|Token
import|;
end_import

begin_class
DECL|class|TestQueryParser
specifier|public
class|class
name|TestQueryParser
extends|extends
name|TestCase
block|{
DECL|method|TestQueryParser
specifier|public
name|TestQueryParser
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|field|qpAnalyzer
specifier|public
specifier|static
name|Analyzer
name|qpAnalyzer
init|=
operator|new
name|QPTestAnalyzer
argument_list|()
decl_stmt|;
DECL|class|QPTestFilter
specifier|public
specifier|static
class|class
name|QPTestFilter
extends|extends
name|TokenFilter
block|{
comment|/**      * Filter which discards the token 'stop' and which expands the      * token 'phrase' into 'phrase1 phrase2'      */
DECL|method|QPTestFilter
specifier|public
name|QPTestFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|input
operator|=
name|in
expr_stmt|;
block|}
DECL|field|inPhrase
name|boolean
name|inPhrase
init|=
literal|false
decl_stmt|;
DECL|field|savedStart
DECL|field|savedEnd
name|int
name|savedStart
init|=
literal|0
decl_stmt|,
name|savedEnd
init|=
literal|0
decl_stmt|;
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|inPhrase
condition|)
block|{
name|inPhrase
operator|=
literal|false
expr_stmt|;
return|return
operator|new
name|Token
argument_list|(
literal|"phrase2"
argument_list|,
name|savedStart
argument_list|,
name|savedEnd
argument_list|)
return|;
block|}
else|else
for|for
control|(
name|Token
name|token
init|=
name|input
operator|.
name|next
argument_list|()
init|;
name|token
operator|!=
literal|null
condition|;
name|token
operator|=
name|input
operator|.
name|next
argument_list|()
control|)
if|if
condition|(
name|token
operator|.
name|termText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"phrase"
argument_list|)
condition|)
block|{
name|inPhrase
operator|=
literal|true
expr_stmt|;
name|savedStart
operator|=
name|token
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|savedEnd
operator|=
name|token
operator|.
name|endOffset
argument_list|()
expr_stmt|;
return|return
operator|new
name|Token
argument_list|(
literal|"phrase1"
argument_list|,
name|savedStart
argument_list|,
name|savedEnd
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|token
operator|.
name|termText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"stop"
argument_list|)
condition|)
return|return
name|token
return|;
return|return
literal|null
return|;
block|}
block|}
DECL|class|QPTestAnalyzer
specifier|public
specifier|static
class|class
name|QPTestAnalyzer
extends|extends
name|Analyzer
block|{
DECL|method|QPTestAnalyzer
specifier|public
name|QPTestAnalyzer
parameter_list|()
block|{     }
comment|/** Filters LowerCaseTokenizer with StopFilter. */
DECL|method|tokenStream
specifier|public
specifier|final
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|QPTestFilter
argument_list|(
operator|new
name|LowerCaseTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**     * initialize this TemplateTester by creating a WebMacro instance     * and a default Context.     */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{   }
DECL|method|assertQueryEquals
specifier|public
name|void
name|assertQueryEquals
parameter_list|(
name|String
name|query
parameter_list|,
name|Analyzer
name|a
parameter_list|,
name|String
name|result
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|a
operator|==
literal|null
condition|)
name|a
operator|=
operator|new
name|SimpleAnalyzer
argument_list|()
expr_stmt|;
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
literal|"field"
argument_list|,
name|a
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|q
operator|.
name|toString
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|s
operator|.
name|equals
argument_list|(
name|result
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Query /"
operator|+
name|query
operator|+
literal|"/ yielded /"
operator|+
name|s
operator|+
literal|"/, expecting /"
operator|+
name|result
operator|+
literal|"/"
argument_list|)
expr_stmt|;
assert|assert
operator|(
literal|false
operator|)
assert|;
block|}
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQueryEquals
argument_list|(
literal|"term term term"
argument_list|,
literal|null
argument_list|,
literal|"term term term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term term1 term2"
argument_list|,
literal|null
argument_list|,
literal|"term term term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term 1.0 1 2"
argument_list|,
literal|null
argument_list|,
literal|"term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a AND b"
argument_list|,
literal|null
argument_list|,
literal|"+a +b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a AND NOT b"
argument_list|,
literal|null
argument_list|,
literal|"+a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a AND -b"
argument_list|,
literal|null
argument_list|,
literal|"+a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a AND !b"
argument_list|,
literal|null
argument_list|,
literal|"+a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a&& b"
argument_list|,
literal|null
argument_list|,
literal|"+a +b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a&&b"
argument_list|,
literal|null
argument_list|,
literal|"+a +b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a&& ! b"
argument_list|,
literal|null
argument_list|,
literal|"+a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a OR b"
argument_list|,
literal|null
argument_list|,
literal|"a b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a || b"
argument_list|,
literal|null
argument_list|,
literal|"a b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a OR !b"
argument_list|,
literal|null
argument_list|,
literal|"a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a OR ! b"
argument_list|,
literal|null
argument_list|,
literal|"a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a OR -b"
argument_list|,
literal|null
argument_list|,
literal|"a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"+term -term term"
argument_list|,
literal|null
argument_list|,
literal|"+term -term term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"foo:term AND field:anotherTerm"
argument_list|,
literal|null
argument_list|,
literal|"+foo:term +anotherterm"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term AND \"phrase phrase\""
argument_list|,
literal|null
argument_list|,
literal|"+term +\"phrase phrase\""
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"germ term^2.0"
argument_list|,
literal|null
argument_list|,
literal|"germ term^2.0"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term^2.0"
argument_list|,
literal|null
argument_list|,
literal|"term^2.0"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"(foo OR bar) AND (baz OR boo)"
argument_list|,
literal|null
argument_list|,
literal|"+(foo bar) +(baz boo)"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"((a OR b) AND NOT c) OR d"
argument_list|,
literal|null
argument_list|,
literal|"(+(a b) -c) d"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"+(apple \"steve jobs\") -(foo bar baz)"
argument_list|,
literal|null
argument_list|,
literal|"+(apple \"steve jobs\") -(foo bar baz)"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"+title:(dog OR cat) -author:\"bob dole\""
argument_list|,
literal|null
argument_list|,
literal|"+(title:dog title:cat) -author:\"bob dole\""
argument_list|)
expr_stmt|;
block|}
DECL|method|testQPA
specifier|public
name|void
name|testQPA
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQueryEquals
argument_list|(
literal|"term term term"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|"term term term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term +stop term"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|"term term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term -stop term"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|"term term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"drop AND stop AND roll"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|"+drop +roll"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term phrase term"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|"term \"phrase1 phrase2\" term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term AND NOT phrase term"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|"+term -\"phrase1 phrase2\" term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"stop"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

