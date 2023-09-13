package de.helicopter_vs_aliens.util.geometry;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class PointTest {

    private final Point
            point1 = Point.newInstance(1, 2);

    private final Point
            point2 = Point.newInstance(1, 2);


    @Test
    void shouldRecognizeEqualPointsAsEqual()
    {
        assertThat(point1).isEqualTo(point2);
    }
}