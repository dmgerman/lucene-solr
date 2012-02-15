begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.uima
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|uima
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Tokenizer
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
name|OffsetAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|analysis_engine
operator|.
name|AnalysisEngineProcessException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|cas
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|cas
operator|.
name|text
operator|.
name|AnnotationFS
import|;
end_import

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
name|Reader
import|;
end_import

begin_comment
comment|/**  * a {@link Tokenizer} which creates tokens from UIMA Annotations  */
end_comment

begin_class
DECL|class|UIMAAnnotationsTokenizer
specifier|public
specifier|final
class|class
name|UIMAAnnotationsTokenizer
extends|extends
name|BaseUIMATokenizer
block|{
DECL|field|termAttr
specifier|private
specifier|final
name|CharTermAttribute
name|termAttr
decl_stmt|;
DECL|field|offsetAttr
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAttr
decl_stmt|;
DECL|field|tokenTypeString
specifier|private
specifier|final
name|String
name|tokenTypeString
decl_stmt|;
DECL|field|finalOffset
specifier|private
name|int
name|finalOffset
init|=
literal|0
decl_stmt|;
DECL|method|UIMAAnnotationsTokenizer
specifier|public
name|UIMAAnnotationsTokenizer
parameter_list|(
name|String
name|descriptorPath
parameter_list|,
name|String
name|tokenType
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|,
name|descriptorPath
argument_list|)
expr_stmt|;
name|this
operator|.
name|tokenTypeString
operator|=
name|tokenType
expr_stmt|;
name|this
operator|.
name|termAttr
operator|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|offsetAttr
operator|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|analyzeText
specifier|private
name|void
name|analyzeText
parameter_list|()
throws|throws
name|IOException
throws|,
name|AnalysisEngineProcessException
block|{
name|analyzeInput
argument_list|()
expr_stmt|;
name|finalOffset
operator|=
name|correctOffset
argument_list|(
name|cas
operator|.
name|getDocumentText
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|Type
name|tokenType
init|=
name|cas
operator|.
name|getTypeSystem
argument_list|()
operator|.
name|getType
argument_list|(
name|tokenTypeString
argument_list|)
decl_stmt|;
name|iterator
operator|=
name|cas
operator|.
name|getAnnotationIndex
argument_list|(
name|tokenType
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|iterator
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|analyzeText
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|AnnotationFS
name|next
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|termAttr
operator|.
name|append
argument_list|(
name|next
operator|.
name|getCoveredText
argument_list|()
argument_list|)
expr_stmt|;
name|offsetAttr
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|next
operator|.
name|getBegin
argument_list|()
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|next
operator|.
name|getEnd
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|offsetAttr
operator|.
name|setOffset
argument_list|(
name|finalOffset
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

