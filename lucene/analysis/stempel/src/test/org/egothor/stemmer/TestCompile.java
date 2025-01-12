begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.egothor.stemmer
package|package
name|org
operator|.
name|egothor
operator|.
name|stemmer
package|;
end_package

begin_comment
comment|/*  Egothor Software License version 1.00  Copyright (C) 1997-2004 Leo Galambos.  Copyright (C) 2002-2004 "Egothor developers"  on behalf of the Egothor Project.  All rights reserved.   This  software  is  copyrighted  by  the "Egothor developers". If this  license applies to a single file or document, the "Egothor developers"  are the people or entities mentioned as copyright holders in that file  or  document.  If  this  license  applies  to the Egothor project as a  whole,  the  copyright holders are the people or entities mentioned in  the  file CREDITS. This file can be found in the same location as this  license in the distribution.   Redistribution  and  use  in  source and binary forms, with or without  modification, are permitted provided that the following conditions are  met:  1. Redistributions  of  source  code  must retain the above copyright  notice, the list of contributors, this list of conditions, and the  following disclaimer.  2. Redistributions  in binary form must reproduce the above copyright  notice, the list of contributors, this list of conditions, and the  disclaimer  that  follows  these  conditions  in the documentation  and/or other materials provided with the distribution.  3. The name "Egothor" must not be used to endorse or promote products  derived  from  this software without prior written permission. For  written permission, please contact Leo.G@seznam.cz  4. Products  derived  from this software may not be called "Egothor",  nor  may  "Egothor"  appear  in  their name, without prior written  permission from Leo.G@seznam.cz.   In addition, we request that you include in the end-user documentation  provided  with  the  redistribution  and/or  in the software itself an  acknowledgement equivalent to the following:  "This product includes software developed by the Egothor Project.  http://egothor.sf.net/"   THIS  SOFTWARE  IS  PROVIDED  ``AS  IS''  AND ANY EXPRESSED OR IMPLIED  WARRANTIES,  INCLUDING,  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF  MERCHANTABILITY  AND  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN  NO  EVENT  SHALL THE EGOTHOR PROJECT OR ITS CONTRIBUTORS BE LIABLE  FOR   ANY   DIRECT,   INDIRECT,  INCIDENTAL,  SPECIAL,  EXEMPLARY,  OR  CONSEQUENTIAL  DAMAGES  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF  SUBSTITUTE  GOODS  OR  SERVICES;  LOSS  OF  USE,  DATA, OR PROFITS; OR  BUSINESS  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  WHETHER  IN  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE  OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN  IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.   This  software  consists  of  voluntary  contributions  made  by  many  individuals  on  behalf  of  the  Egothor  Project  and was originally  created by Leo Galambos (Leo.G@seznam.cz).  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|LineNumberReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Files
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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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

begin_class
DECL|class|TestCompile
specifier|public
class|class
name|TestCompile
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCompile
specifier|public
name|void
name|testCompile
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|dir
init|=
name|createTempDir
argument_list|(
literal|"testCompile"
argument_list|)
decl_stmt|;
name|Path
name|output
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"testRules.txt"
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|input
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"testRules.txt"
argument_list|)
init|)
block|{
name|Files
operator|.
name|copy
argument_list|(
name|input
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
name|String
name|path
init|=
name|output
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Compile
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"test"
block|,
name|path
block|}
argument_list|)
expr_stmt|;
name|Path
name|compiled
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"testRules.txt.out"
argument_list|)
decl_stmt|;
name|Trie
name|trie
init|=
name|loadTrie
argument_list|(
name|compiled
argument_list|)
decl_stmt|;
name|assertTrie
argument_list|(
name|trie
argument_list|,
name|output
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrie
argument_list|(
name|trie
argument_list|,
name|output
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testCompileBackwards
specifier|public
name|void
name|testCompileBackwards
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|dir
init|=
name|createTempDir
argument_list|(
literal|"testCompile"
argument_list|)
decl_stmt|;
name|Path
name|output
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"testRules.txt"
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|input
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"testRules.txt"
argument_list|)
init|)
block|{
name|Files
operator|.
name|copy
argument_list|(
name|input
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
name|String
name|path
init|=
name|output
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Compile
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-test"
block|,
name|path
block|}
argument_list|)
expr_stmt|;
name|Path
name|compiled
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"testRules.txt.out"
argument_list|)
decl_stmt|;
name|Trie
name|trie
init|=
name|loadTrie
argument_list|(
name|compiled
argument_list|)
decl_stmt|;
name|assertTrie
argument_list|(
name|trie
argument_list|,
name|output
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrie
argument_list|(
name|trie
argument_list|,
name|output
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testCompileMulti
specifier|public
name|void
name|testCompileMulti
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|dir
init|=
name|createTempDir
argument_list|(
literal|"testCompile"
argument_list|)
decl_stmt|;
name|Path
name|output
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"testRules.txt"
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|input
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"testRules.txt"
argument_list|)
init|)
block|{
name|Files
operator|.
name|copy
argument_list|(
name|input
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
name|String
name|path
init|=
name|output
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Compile
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"Mtest"
block|,
name|path
block|}
argument_list|)
expr_stmt|;
name|Path
name|compiled
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"testRules.txt.out"
argument_list|)
decl_stmt|;
name|Trie
name|trie
init|=
name|loadTrie
argument_list|(
name|compiled
argument_list|)
decl_stmt|;
name|assertTrie
argument_list|(
name|trie
argument_list|,
name|output
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrie
argument_list|(
name|trie
argument_list|,
name|output
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|loadTrie
specifier|static
name|Trie
name|loadTrie
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|Trie
name|trie
decl_stmt|;
name|DataInputStream
name|is
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|method
init|=
name|is
operator|.
name|readUTF
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|.
name|indexOf
argument_list|(
literal|'M'
argument_list|)
operator|<
literal|0
condition|)
block|{
name|trie
operator|=
operator|new
name|Trie
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|trie
operator|=
operator|new
name|MultiTrie
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|trie
return|;
block|}
DECL|method|assertTrie
specifier|private
specifier|static
name|void
name|assertTrie
parameter_list|(
name|Trie
name|trie
parameter_list|,
name|Path
name|file
parameter_list|,
name|boolean
name|usefull
parameter_list|,
name|boolean
name|storeorig
parameter_list|)
throws|throws
name|Exception
block|{
name|LineNumberReader
name|in
init|=
operator|new
name|LineNumberReader
argument_list|(
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|file
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
init|=
name|in
operator|.
name|readLine
argument_list|()
init|;
name|line
operator|!=
literal|null
condition|;
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
control|)
block|{
try|try
block|{
name|line
operator|=
name|line
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|String
name|stem
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|storeorig
condition|)
block|{
name|CharSequence
name|cmd
init|=
operator|(
name|usefull
operator|)
condition|?
name|trie
operator|.
name|getFully
argument_list|(
name|stem
argument_list|)
else|:
name|trie
operator|.
name|getLastOnPath
argument_list|(
name|stem
argument_list|)
decl_stmt|;
name|StringBuilder
name|stm
init|=
operator|new
name|StringBuilder
argument_list|(
name|stem
argument_list|)
decl_stmt|;
name|Diff
operator|.
name|apply
argument_list|(
name|stm
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stem
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|stm
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|token
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|.
name|equals
argument_list|(
name|stem
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|CharSequence
name|cmd
init|=
operator|(
name|usefull
operator|)
condition|?
name|trie
operator|.
name|getFully
argument_list|(
name|token
argument_list|)
else|:
name|trie
operator|.
name|getLastOnPath
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|StringBuilder
name|stm
init|=
operator|new
name|StringBuilder
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|Diff
operator|.
name|apply
argument_list|(
name|stm
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stem
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|stm
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
name|x
parameter_list|)
block|{
comment|// no base token (stem) on a line
block|}
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

