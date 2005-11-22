begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|WhitespaceAnalyzer
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
name|WhitespaceTokenizer
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Similarity
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
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|TestDocumentWriter
specifier|public
class|class
name|TestDocumentWriter
extends|extends
name|TestCase
block|{
DECL|field|dir
specifier|private
name|RAMDirectory
name|dir
decl_stmt|;
DECL|method|TestDocumentWriter
specifier|public
name|TestDocumentWriter
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
name|dir
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{    }
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddDocument
specifier|public
name|void
name|testAddDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|testDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
decl_stmt|;
name|Similarity
name|similarity
init|=
name|Similarity
operator|.
name|getDefault
argument_list|()
decl_stmt|;
name|DocumentWriter
name|writer
init|=
operator|new
name|DocumentWriter
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
name|similarity
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|String
name|segName
init|=
literal|"test"
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|segName
argument_list|,
name|testDoc
argument_list|)
expr_stmt|;
comment|//After adding the document, we should be able to read it back in
name|SegmentReader
name|reader
init|=
name|SegmentReader
operator|.
name|get
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|segName
argument_list|,
literal|1
argument_list|,
name|dir
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//System.out.println("Document: " + doc);
name|Field
index|[]
name|fields
init|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"textField2"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fields
operator|!=
literal|null
operator|&&
name|fields
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|FIELD_2_TEXT
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|isTermVectorStored
argument_list|()
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"textField1"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
operator|!=
literal|null
operator|&&
name|fields
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|FIELD_1_TEXT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|isTermVectorStored
argument_list|()
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"keyField"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
operator|!=
literal|null
operator|&&
name|fields
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|KEYWORD_TEXT
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc
operator|.
name|getFields
argument_list|(
name|DocHelper
operator|.
name|NO_NORMS_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
operator|!=
literal|null
operator|&&
name|fields
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|NO_NORMS_TEXT
argument_list|)
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc
operator|.
name|getFields
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_3_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
operator|!=
literal|null
operator|&&
name|fields
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fields
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
name|DocHelper
operator|.
name|FIELD_3_TEXT
argument_list|)
argument_list|)
expr_stmt|;
comment|// test that the norm file is not present if omitNorms is true
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|reader
operator|.
name|fieldInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|fi
init|=
name|reader
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|isIndexed
condition|)
block|{
name|assertTrue
argument_list|(
name|fi
operator|.
name|omitNorms
operator|==
operator|!
name|dir
operator|.
name|fileExists
argument_list|(
name|segName
operator|+
literal|".f"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testPositionIncrementGap
specifier|public
name|void
name|testPositionIncrementGap
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
specifier|public
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
name|WhitespaceTokenizer
argument_list|(
name|reader
argument_list|)
return|;
block|}
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
literal|500
return|;
block|}
block|}
decl_stmt|;
name|Similarity
name|similarity
init|=
name|Similarity
operator|.
name|getDefault
argument_list|()
decl_stmt|;
name|DocumentWriter
name|writer
init|=
operator|new
name|DocumentWriter
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
name|similarity
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"repeated"
argument_list|,
literal|"repeated one"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"repeated"
argument_list|,
literal|"repeated two"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|segName
init|=
literal|"test"
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|segName
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|SegmentReader
name|reader
init|=
name|SegmentReader
operator|.
name|get
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|segName
argument_list|,
literal|1
argument_list|,
name|dir
argument_list|)
argument_list|)
decl_stmt|;
name|TermPositions
name|termPositions
init|=
name|reader
operator|.
name|termPositions
argument_list|(
operator|new
name|Term
argument_list|(
literal|"repeated"
argument_list|,
literal|"repeated"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|termPositions
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|freq
init|=
name|termPositions
operator|.
name|freq
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|502
argument_list|,
name|termPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

