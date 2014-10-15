begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|codecs
operator|.
name|Codec
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
name|BaseNormsFormatTestCase
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
name|DirectoryReader
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
name|IndexReader
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
name|MultiDocValues
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
name|NumericDocValues
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
name|Directory
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
name|FSDirectory
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
name|TestUtil
import|;
end_import

begin_comment
comment|/** Tests Lucene40's norms format */
end_comment

begin_class
DECL|class|TestLucene40NormsFormat
specifier|public
class|class
name|TestLucene40NormsFormat
extends|extends
name|BaseNormsFormatTestCase
block|{
DECL|field|codec
specifier|final
name|Codec
name|codec
init|=
operator|new
name|Lucene40RWCodec
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getCodec
specifier|protected
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
comment|/** Copy this back to /l/400/lucene/CreateUndeadNorms.java, then:    *   - ant clean    *   - pushd analysis/common; ant jar; popd    *   - pushd core; ant jar; popd    *   - javac -cp build/analysis/common/lucene-analyzers-common-4.0-SNAPSHOT.jar:build/core/lucene-core-4.0-SNAPSHOT.jar CreateUndeadNorms.java    *   - java -cp .:build/analysis/common/lucene-analyzers-common-4.0-SNAPSHOT.jar:build/core/lucene-core-4.0-SNAPSHOT.jar CreateUndeadNorms    *   - cd /tmp/undeadnorms  ; zip index.40.undeadnorms.zip *  import java.io.File; import java.io.IOException;  import org.apache.lucene.analysis.TokenStream; import org.apache.lucene.analysis.core.WhitespaceAnalyzer; import org.apache.lucene.document.Document; import org.apache.lucene.document.Field; import org.apache.lucene.document.StringField; import org.apache.lucene.document.TextField; import org.apache.lucene.index.IndexWriter; import org.apache.lucene.index.IndexWriterConfig; import org.apache.lucene.index.Term; import org.apache.lucene.store.Directory; import org.apache.lucene.store.FSDirectory; import org.apache.lucene.util.Version;  public class CreateUndeadNorms {   public static void main(String[] args) throws Exception {     File file = new File("/tmp/undeadnorms");     if (file.exists()) {       throw new RuntimeException("please remove /tmp/undeadnorms first");     }     Directory dir = FSDirectory.open(new File("/tmp/undeadnorms"));     IndexWriter w = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_40, new WhitespaceAnalyzer(Version.LUCENE_40)));     Document doc = new Document();     doc.add(new StringField("id", "0", Field.Store.NO));     w.addDocument(doc);     doc = new Document();     doc.add(new StringField("id", "1", Field.Store.NO));     Field content = new TextField("content", "some content", Field.Store.NO);     content.setTokenStream(new TokenStream() {         @Override         public boolean incrementToken() throws IOException {           throw new IOException("brains brains!");         }       });      doc.add(content);     try {       w.addDocument(doc);       throw new RuntimeException("didn't hit exception");     } catch (IOException ioe) {       // perfect     }     w.close();     dir.close();   } } */
comment|/**     * LUCENE-6006: Test undead norms.    *                                 .....                *                             C C  /                *                            /<   /                 *             ___ __________/_#__=o                 *            /(- /(\_\________   \                  *            \ ) \ )_      \o     \                 *            /|\ /|\       |'     |                 *                          |     _|                 *                          /o   __\                 *                         / '     |                 *                        / /      |                 *                       /_/\______|                 *                      (   _(<                  *                       \    \    \                 *                        \    \    |                *                         \____\____\               *                         ____\_\__\_\              *                       /`   /`     o\              *                       |___ |_______|    *    */
DECL|method|testReadUndeadNorms
specifier|public
name|void
name|testReadUndeadNorms
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|resource
init|=
name|TestLucene40NormsFormat
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"index.40.undeadnorms.zip"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
name|createTempDir
argument_list|(
literal|"undeadnorms"
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|unzip
argument_list|(
name|resource
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|NumericDocValues
name|undeadNorms
init|=
name|MultiDocValues
operator|.
name|getNormValues
argument_list|(
name|r
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|undeadNorms
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|undeadNorms
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|undeadNorms
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

