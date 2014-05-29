/*
 * Copyright (c) 2008-2014 wetator.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wetator.backend.htmlunit.content;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.IContent.ContentType;

/**
 * @author rbri
 */
public class ContentTypeUtilTest {

  @Test
  public void getContentTypeForFileName() {
    ContentType tmpType = ContentTypeUtil.getContentTypeForFileName(null);
    Assert.assertEquals(ContentType.OTHER, tmpType);
    tmpType = ContentTypeUtil.getContentTypeForFileName("");
    Assert.assertEquals(ContentType.OTHER, tmpType);
    tmpType = ContentTypeUtil.getContentTypeForFileName("abc");
    Assert.assertEquals(ContentType.OTHER, tmpType);
    tmpType = ContentTypeUtil.getContentTypeForFileName("abc.def");
    Assert.assertEquals(ContentType.OTHER, tmpType);

    tmpType = ContentTypeUtil.getContentTypeForFileName("abc.pdf");
    Assert.assertEquals(ContentType.PDF, tmpType);

    tmpType = ContentTypeUtil.getContentTypeForFileName("Test File.XLS");
    Assert.assertEquals(ContentType.XLS, tmpType);

    tmpType = ContentTypeUtil.getContentTypeForFileName("test.zip");
    Assert.assertEquals(ContentType.ZIP, tmpType);
  }
}
