begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|Term
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
name|IndexWriter
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
name|Hits
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
name|store
operator|.
name|RAMDirectory
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
name|SimpleAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/** Document boost unit test.   *   * @author Doug Cutting   * @version $Revision$   */
end_comment

begin_class
DECL|class|TestDocBoost
specifier|public
class|class
name|TestDocBoost
extends|extends
name|TestCase
block|{
DECL|method|TestDocBoost
specifier|public
name|TestDocBoost
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
DECL|method|test
specifier|public
specifier|static
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|store
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|store
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Field
name|f1
init|=
name|Field
operator|.
name|Text
argument_list|(
literal|"field"
argument_list|,
literal|"word"
argument_list|)
decl_stmt|;
name|Field
name|f2
init|=
name|Field
operator|.
name|Text
argument_list|(
literal|"field"
argument_list|,
literal|"word"
argument_list|)
decl_stmt|;
name|f2
operator|.
name|setBoost
argument_list|(
literal|2.0f
argument_list|)
expr_stmt|;
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|d2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|d3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|d4
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d3
operator|.
name|setBoost
argument_list|(
literal|3.0f
argument_list|)
expr_stmt|;
name|d4
operator|.
name|setBoost
argument_list|(
literal|2.0f
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
comment|// boost = 1
name|d2
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
comment|// boost = 2
name|d3
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
comment|// boost = 3
name|d4
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
comment|// boost = 4
name|writer
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d4
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[
literal|4
index|]
decl_stmt|;
operator|new
name|IndexSearcher
argument_list|(
name|store
argument_list|)
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"word"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|HitCollector
argument_list|()
block|{
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|scores
index|[
name|doc
index|]
operator|=
name|score
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|float
name|lastScore
init|=
literal|0.0f
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
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|scores
index|[
name|i
index|]
operator|>
name|lastScore
argument_list|)
expr_stmt|;
name|lastScore
operator|=
name|scores
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

