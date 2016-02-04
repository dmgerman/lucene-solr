begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.byTask.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|utils
package|;
end_package

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
name|util
operator|.
name|CharFilterFactory
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
name|util
operator|.
name|TokenFilterFactory
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
name|util
operator|.
name|TokenizerFactory
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A factory to create an analyzer.  * See {@link org.apache.lucene.benchmark.byTask.tasks.AnalyzerFactoryTask}  */
end_comment

begin_class
DECL|class|AnalyzerFactory
specifier|public
specifier|final
class|class
name|AnalyzerFactory
block|{
DECL|field|charFilterFactories
specifier|final
specifier|private
name|List
argument_list|<
name|CharFilterFactory
argument_list|>
name|charFilterFactories
decl_stmt|;
DECL|field|tokenizerFactory
specifier|final
specifier|private
name|TokenizerFactory
name|tokenizerFactory
decl_stmt|;
DECL|field|tokenFilterFactories
specifier|final
specifier|private
name|List
argument_list|<
name|TokenFilterFactory
argument_list|>
name|tokenFilterFactories
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
DECL|field|positionIncrementGap
specifier|private
name|Integer
name|positionIncrementGap
init|=
literal|null
decl_stmt|;
DECL|field|offsetGap
specifier|private
name|Integer
name|offsetGap
init|=
literal|null
decl_stmt|;
DECL|method|AnalyzerFactory
specifier|public
name|AnalyzerFactory
parameter_list|(
name|List
argument_list|<
name|CharFilterFactory
argument_list|>
name|charFilterFactories
parameter_list|,
name|TokenizerFactory
name|tokenizerFactory
parameter_list|,
name|List
argument_list|<
name|TokenFilterFactory
argument_list|>
name|tokenFilterFactories
parameter_list|)
block|{
name|this
operator|.
name|charFilterFactories
operator|=
name|charFilterFactories
expr_stmt|;
assert|assert
literal|null
operator|!=
name|tokenizerFactory
assert|;
name|this
operator|.
name|tokenizerFactory
operator|=
name|tokenizerFactory
expr_stmt|;
name|this
operator|.
name|tokenFilterFactories
operator|=
name|tokenFilterFactories
expr_stmt|;
block|}
DECL|method|setName
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|setPositionIncrementGap
specifier|public
name|void
name|setPositionIncrementGap
parameter_list|(
name|Integer
name|positionIncrementGap
parameter_list|)
block|{
name|this
operator|.
name|positionIncrementGap
operator|=
name|positionIncrementGap
expr_stmt|;
block|}
DECL|method|setOffsetGap
specifier|public
name|void
name|setOffsetGap
parameter_list|(
name|Integer
name|offsetGap
parameter_list|)
block|{
name|this
operator|.
name|offsetGap
operator|=
name|offsetGap
expr_stmt|;
block|}
DECL|method|create
specifier|public
name|Analyzer
name|create
parameter_list|()
block|{
return|return
operator|new
name|Analyzer
argument_list|()
block|{
specifier|private
specifier|final
name|Integer
name|positionIncrementGap
init|=
name|AnalyzerFactory
operator|.
name|this
operator|.
name|positionIncrementGap
decl_stmt|;
specifier|private
specifier|final
name|Integer
name|offsetGap
init|=
name|AnalyzerFactory
operator|.
name|this
operator|.
name|offsetGap
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Reader
name|initReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|charFilterFactories
operator|!=
literal|null
operator|&&
name|charFilterFactories
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Reader
name|wrappedReader
init|=
name|reader
decl_stmt|;
for|for
control|(
name|CharFilterFactory
name|charFilterFactory
range|:
name|charFilterFactories
control|)
block|{
name|wrappedReader
operator|=
name|charFilterFactory
operator|.
name|create
argument_list|(
name|wrappedReader
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|wrappedReader
expr_stmt|;
block|}
return|return
name|reader
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Analyzer
operator|.
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
specifier|final
name|Tokenizer
name|tokenizer
init|=
name|tokenizerFactory
operator|.
name|create
argument_list|()
decl_stmt|;
name|TokenStream
name|tokenStream
init|=
name|tokenizer
decl_stmt|;
for|for
control|(
name|TokenFilterFactory
name|filterFactory
range|:
name|tokenFilterFactories
control|)
block|{
name|tokenStream
operator|=
name|filterFactory
operator|.
name|create
argument_list|(
name|tokenStream
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenStream
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
literal|null
operator|==
name|positionIncrementGap
condition|?
name|super
operator|.
name|getPositionIncrementGap
argument_list|(
name|fieldName
argument_list|)
else|:
name|positionIncrementGap
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOffsetGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
literal|null
operator|==
name|offsetGap
condition|?
name|super
operator|.
name|getOffsetGap
argument_list|(
name|fieldName
argument_list|)
else|:
name|offsetGap
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"AnalyzerFactory("
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|name
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"name:"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|positionIncrementGap
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"positionIncrementGap:"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|positionIncrementGap
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|offsetGap
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"offsetGap:"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|offsetGap
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|CharFilterFactory
name|charFilterFactory
range|:
name|charFilterFactories
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|charFilterFactory
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|tokenizerFactory
argument_list|)
expr_stmt|;
for|for
control|(
name|TokenFilterFactory
name|tokenFilterFactory
range|:
name|tokenFilterFactories
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|tokenFilterFactory
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

