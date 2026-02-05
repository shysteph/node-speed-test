package com.generalcode.nodespeedtest;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(2)
public class SpeedTest {
  public int iterateDumb(Element element) {
    NodeList children = element.getChildNodes();
    int count = 0;
    for(int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if(child.getNodeType() == Node.ELEMENT_NODE) {
        count++;
      }
    }
    return count;
  }

  public int iterateSmart(Element element) {
    NodeList children = element.getChildNodes();
    int count = 0;
    for(int i = 0, len = children.getLength(); i < len; i++) {
      Node child = children.item(i);
      if(child.getNodeType() == Node.ELEMENT_NODE) {
        count++;
      }
    }
    return count;
  }

  public int iterateNextSibling(Element element) {
    Node child = element.getFirstChild();
    int count = 0;
    while(child != null) {
      if(child.getNodeType() == Node.ELEMENT_NODE) {
        count++;
      }
      child = child.getNextSibling();
    }
    return count;
  }

  @Benchmark
  public void benchmarkDumb(BenchmarkState state, Blackhole blackhole) {
    blackhole.consume(iterateDumb(state.element));
  }

  @Benchmark
  public void benchmarkSmart(BenchmarkState state, Blackhole blackhole) {
    blackhole.consume(iterateSmart(state.element));
  }

  @Benchmark
  public void benchmarkNextSibling(BenchmarkState state, Blackhole blackhole) {
    blackhole.consume(iterateNextSibling(state.element));
  }

  @State(Scope.Thread)
  public static class BenchmarkState {
    Element element;

    @Setup
    public void prepare() {
      try {
        // 1. Instantiate DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // 2. Instantiate DocumentBuilder
        DocumentBuilder builder = factory.newDocumentBuilder();
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("bigxml.xml")) {
          // 3. Parse the XML file into a Document object
          Document doc = builder.parse(is);
          element = doc.getDocumentElement();
        }

      } catch (ParserConfigurationException | SAXException | IOException e) {
        e.printStackTrace();
      }
    }
  }
}

