begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

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
name|Comparator
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
name|document
operator|.
name|NearestNeighbor
operator|.
name|NearestHit
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
name|geo
operator|.
name|GeoEncodingUtils
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
name|geo
operator|.
name|GeoTestUtil
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
name|index
operator|.
name|IndexWriterConfig
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
name|RandomIndexWriter
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
name|SerialMergeScheduler
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|FieldDoc
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
name|MatchAllDocsQuery
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
name|ScoreDoc
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
name|Sort
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
name|TopFieldDocs
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
name|util
operator|.
name|LuceneTestCase
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
name|SloppyMath
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

begin_class
DECL|class|TestNearest
specifier|public
class|class
name|TestNearest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testNearestNeighborWithDeletedDocs
specifier|public
name|void
name|testNearestNeighborWithDeletedDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|getIndexWriterConfig
argument_list|()
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
name|LatLonPoint
argument_list|(
literal|"point"
argument_list|,
literal|40.0
argument_list|,
literal|50.0
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"point"
argument_list|,
literal|45.0
argument_list|,
literal|55.0
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
comment|// can't wrap because we require Lucene60PointsFormat directly but e.g. ParallelReader wraps with its own points impl:
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|FieldDoc
name|hit
init|=
operator|(
name|FieldDoc
operator|)
name|LatLonPoint
operator|.
name|nearest
argument_list|(
name|s
argument_list|,
literal|"point"
argument_list|,
literal|40.0
argument_list|,
literal|50.0
argument_list|,
literal|1
argument_list|)
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|r
operator|.
name|document
argument_list|(
name|hit
operator|.
name|doc
argument_list|)
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
name|w
operator|.
name|getReader
argument_list|()
expr_stmt|;
comment|// can't wrap because we require Lucene60PointsFormat directly but e.g. ParallelReader wraps with its own points impl:
name|s
operator|=
name|newSearcher
argument_list|(
name|r
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|hit
operator|=
operator|(
name|FieldDoc
operator|)
name|LatLonPoint
operator|.
name|nearest
argument_list|(
name|s
argument_list|,
literal|"point"
argument_list|,
literal|40.0
argument_list|,
literal|50.0
argument_list|,
literal|1
argument_list|)
operator|.
name|scoreDocs
index|[
literal|0
index|]
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|r
operator|.
name|document
argument_list|(
name|hit
operator|.
name|doc
argument_list|)
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNearestNeighborWithAllDeletedDocs
specifier|public
name|void
name|testNearestNeighborWithAllDeletedDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|getIndexWriterConfig
argument_list|()
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
name|LatLonPoint
argument_list|(
literal|"point"
argument_list|,
literal|40.0
argument_list|,
literal|50.0
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"point"
argument_list|,
literal|45.0
argument_list|,
literal|55.0
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
comment|// can't wrap because we require Lucene60PointsFormat directly but e.g. ParallelReader wraps with its own points impl:
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|FieldDoc
name|hit
init|=
operator|(
name|FieldDoc
operator|)
name|LatLonPoint
operator|.
name|nearest
argument_list|(
name|s
argument_list|,
literal|"point"
argument_list|,
literal|40.0
argument_list|,
literal|50.0
argument_list|,
literal|1
argument_list|)
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|r
operator|.
name|document
argument_list|(
name|hit
operator|.
name|doc
argument_list|)
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
name|w
operator|.
name|getReader
argument_list|()
expr_stmt|;
comment|// can't wrap because we require Lucene60PointsFormat directly but e.g. ParallelReader wraps with its own points impl:
name|s
operator|=
name|newSearcher
argument_list|(
name|r
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|LatLonPoint
operator|.
name|nearest
argument_list|(
name|s
argument_list|,
literal|"point"
argument_list|,
literal|40.0
argument_list|,
literal|50.0
argument_list|,
literal|1
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testTieBreakByDocID
specifier|public
name|void
name|testTieBreakByDocID
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|getIndexWriterConfig
argument_list|()
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
name|LatLonPoint
argument_list|(
literal|"point"
argument_list|,
literal|40.0
argument_list|,
literal|50.0
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"point"
argument_list|,
literal|40.0
argument_list|,
literal|50.0
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
comment|// can't wrap because we require Lucene60PointsFormat directly but e.g. ParallelReader wraps with its own points impl:
name|ScoreDoc
index|[]
name|hits
init|=
name|LatLonPoint
operator|.
name|nearest
argument_list|(
name|newSearcher
argument_list|(
name|r
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|"point"
argument_list|,
literal|45.0
argument_list|,
literal|50.0
argument_list|,
literal|2
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|r
operator|.
name|document
argument_list|(
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|r
operator|.
name|document
argument_list|(
name|hits
index|[
literal|1
index|]
operator|.
name|doc
argument_list|)
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNearestNeighborWithNoDocs
specifier|public
name|void
name|testNearestNeighborWithNoDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|getIndexWriterConfig
argument_list|()
argument_list|)
decl_stmt|;
name|DirectoryReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
comment|// can't wrap because we require Lucene60PointsFormat directly but e.g. ParallelReader wraps with its own points impl:
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|LatLonPoint
operator|.
name|nearest
argument_list|(
name|newSearcher
argument_list|(
name|r
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|"point"
argument_list|,
literal|40.0
argument_list|,
literal|50.0
argument_list|,
literal|1
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|quantizeLat
specifier|private
name|double
name|quantizeLat
parameter_list|(
name|double
name|latRaw
parameter_list|)
block|{
return|return
name|GeoEncodingUtils
operator|.
name|decodeLatitude
argument_list|(
name|GeoEncodingUtils
operator|.
name|encodeLatitude
argument_list|(
name|latRaw
argument_list|)
argument_list|)
return|;
block|}
DECL|method|quantizeLon
specifier|private
name|double
name|quantizeLon
parameter_list|(
name|double
name|lonRaw
parameter_list|)
block|{
return|return
name|GeoEncodingUtils
operator|.
name|decodeLongitude
argument_list|(
name|GeoEncodingUtils
operator|.
name|encodeLongitude
argument_list|(
name|lonRaw
argument_list|)
argument_list|)
return|;
block|}
DECL|method|testNearestNeighborRandom
specifier|public
name|void
name|testNearestNeighborRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numPoints
init|=
name|atLeast
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|Directory
name|dir
decl_stmt|;
if|if
condition|(
name|numPoints
operator|>
literal|100000
condition|)
block|{
name|dir
operator|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
block|}
name|double
index|[]
name|lats
init|=
operator|new
name|double
index|[
name|numPoints
index|]
decl_stmt|;
name|double
index|[]
name|lons
init|=
operator|new
name|double
index|[
name|numPoints
index|]
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|getIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|numPoints
condition|;
name|id
operator|++
control|)
block|{
name|lats
index|[
name|id
index|]
operator|=
name|quantizeLat
argument_list|(
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
argument_list|)
expr_stmt|;
name|lons
index|[
name|id
index|]
operator|=
name|quantizeLon
argument_list|(
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
argument_list|)
expr_stmt|;
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
name|LatLonPoint
argument_list|(
literal|"point"
argument_list|,
name|lats
index|[
name|id
index|]
argument_list|,
name|lons
index|[
name|id
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonDocValuesField
argument_list|(
literal|"point"
argument_list|,
name|lats
index|[
name|id
index|]
argument_list|,
name|lons
index|[
name|id
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|DirectoryReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: reader="
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
comment|// can't wrap because we require Lucene60PointsFormat directly but e.g. ParallelReader wraps with its own points impl:
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: iter="
operator|+
name|iter
argument_list|)
expr_stmt|;
block|}
name|double
name|pointLat
init|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|pointLon
init|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
decl_stmt|;
comment|// dumb brute force search to get the expected result:
name|NearestHit
index|[]
name|expectedHits
init|=
operator|new
name|NearestHit
index|[
name|lats
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
name|lats
operator|.
name|length
condition|;
name|id
operator|++
control|)
block|{
name|NearestHit
name|hit
init|=
operator|new
name|NearestHit
argument_list|()
decl_stmt|;
name|hit
operator|.
name|distanceMeters
operator|=
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|pointLat
argument_list|,
name|pointLon
argument_list|,
name|lats
index|[
name|id
index|]
argument_list|,
name|lons
index|[
name|id
index|]
argument_list|)
expr_stmt|;
name|hit
operator|.
name|docID
operator|=
name|id
expr_stmt|;
name|expectedHits
index|[
name|id
index|]
operator|=
name|hit
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|expectedHits
argument_list|,
operator|new
name|Comparator
argument_list|<
name|NearestHit
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|NearestHit
name|a
parameter_list|,
name|NearestHit
name|b
parameter_list|)
block|{
name|int
name|cmp
init|=
name|Double
operator|.
name|compare
argument_list|(
name|a
operator|.
name|distanceMeters
argument_list|,
name|b
operator|.
name|distanceMeters
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
comment|// tie break by smaller docID:
return|return
name|a
operator|.
name|docID
operator|-
name|b
operator|.
name|docID
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|int
name|topN
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|lats
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nhits for pointLat="
operator|+
name|pointLat
operator|+
literal|" pointLon="
operator|+
name|pointLon
argument_list|)
expr_stmt|;
block|}
comment|// Also test with MatchAllDocsQuery, sorting by distance:
name|TopFieldDocs
name|fieldDocs
init|=
name|s
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|topN
argument_list|,
operator|new
name|Sort
argument_list|(
name|LatLonDocValuesField
operator|.
name|newDistanceSort
argument_list|(
literal|"point"
argument_list|,
name|pointLat
argument_list|,
name|pointLon
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|LatLonPoint
operator|.
name|nearest
argument_list|(
name|s
argument_list|,
literal|"point"
argument_list|,
name|pointLat
argument_list|,
name|pointLon
argument_list|,
name|topN
argument_list|)
operator|.
name|scoreDocs
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
name|topN
condition|;
name|i
operator|++
control|)
block|{
name|NearestHit
name|expected
init|=
name|expectedHits
index|[
name|i
index|]
decl_stmt|;
name|FieldDoc
name|expected2
init|=
operator|(
name|FieldDoc
operator|)
name|fieldDocs
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|FieldDoc
name|actual
init|=
operator|(
name|FieldDoc
operator|)
name|hits
index|[
name|i
index|]
decl_stmt|;
name|Document
name|actualDoc
init|=
name|r
operator|.
name|document
argument_list|(
name|actual
operator|.
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"hit "
operator|+
name|i
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  expected id="
operator|+
name|expected
operator|.
name|docID
operator|+
literal|" lat="
operator|+
name|lats
index|[
name|expected
operator|.
name|docID
index|]
operator|+
literal|" lon="
operator|+
name|lons
index|[
name|expected
operator|.
name|docID
index|]
operator|+
literal|" distance="
operator|+
name|expected
operator|.
name|distanceMeters
operator|+
literal|" meters"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  actual id="
operator|+
name|actualDoc
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|+
literal|" distance="
operator|+
name|actual
operator|.
name|fields
index|[
literal|0
index|]
operator|+
literal|" meters"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
operator|.
name|docID
argument_list|,
name|actual
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|distanceMeters
argument_list|,
operator|(
operator|(
name|Double
operator|)
name|actual
operator|.
name|fields
index|[
literal|0
index|]
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|docID
argument_list|,
name|expected
operator|.
name|docID
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|(
name|Double
operator|)
name|expected2
operator|.
name|fields
index|[
literal|0
index|]
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|expected
operator|.
name|distanceMeters
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getIndexWriterConfig
specifier|private
name|IndexWriterConfig
name|getIndexWriterConfig
parameter_list|()
block|{
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setCodec
argument_list|(
name|Codec
operator|.
name|forName
argument_list|(
literal|"Lucene60"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|iwc
return|;
block|}
block|}
end_class

end_unit

